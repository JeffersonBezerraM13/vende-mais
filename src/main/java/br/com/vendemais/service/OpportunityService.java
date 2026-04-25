package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.opportunity.OpportunityCloseDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityFilterDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.repository.specification.OpportunitySpecification;
import br.com.vendemais.service.exceptions.BusinessRuleException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Orchestrates opportunity lifecycle rules, including pipeline assignment, stage
 * validation, open negotiation checks, and win/loss closing logic.
 */
@Service
@Transactional(readOnly = true)
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final LeadRepository leadRepository;
    private final PipelineRepository pipelineRepository;
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;

    public OpportunityService(
            OpportunityRepository opportunityRepository,
            LeadRepository leadRepository,
            PipelineRepository pipelineRepository,
            StageRepository stageRepository,
            TaskRepository taskRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.leadRepository = leadRepository;
        this.pipelineRepository = pipelineRepository;
        this.stageRepository = stageRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves opportunities in pages, applying optional filters so pipeline and
     * portfolio screens can be searched and narrowed directly by the backend.
     *
     * @param filter optional filtering criteria, such as search term, virtual status
     *               and pipeline identifier
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered opportunity projections mapped to response DTOs
     */
    public Page<OpportunityResponseDTO> findAll(OpportunityFilterDTO filter, Pageable pageable) {
        Page<Opportunity> opportunitiesPage = opportunityRepository.findAll(
                OpportunitySpecification.withFilters(filter),
                pageable
        );

        return opportunitiesPage.map(OpportunityResponseDTO::daEntidade);
    }

    /**
     * Loads a single opportunity so the CRM can display its commercial and
     * pipeline context.
     *
     * @param id identifier of the opportunity to retrieve
     * @return the requested opportunity mapped to the API response DTO
     * @throws ObjectNotFoundException if the opportunity does not exist
     */
    public OpportunityResponseDTO findById(Long id) {
        Opportunity opportunity = findOpportunityById(id);
        return OpportunityResponseDTO.daEntidade(opportunity);
    }

    /**
     * Checks whether a lead already has open negotiations before another
     * opportunity is created for it.
     *
     * @param leadId identifier of the lead being inspected
     * @return {@code true} when the lead has at least one opportunity marked as open
     * @throws IllegalArgumentException if the informed lead id is {@code null}
     * @throws ObjectNotFoundException if the lead does not exist
     */
    public boolean hasOpenOpportunities(Long leadId) {
        if (leadId == null) {
            throw new IllegalArgumentException("O ID do Lead não pode ser nulo.");
        }

        findLeadById(leadId);

        return opportunityRepository.existsByLeadIdAndWonFalseAndClosedAtIsNull(leadId);
    }

    /**
     * Creates a new opportunity and validates that the referenced lead, pipeline,
     * and stage form a consistent CRM context.
     *
     * @param dto payload describing the opportunity to persist
     * @return the persisted opportunity mapped to the API response DTO
     * @throws ObjectNotFoundException if referenced lead, pipeline, or stage data does not exist
     * @throws BusinessRuleException if the selected stage does not belong to the selected pipeline
     */
    @Transactional
    public OpportunityResponseDTO create(OpportunityRequestDTO dto) {
        Lead lead = findLeadById(dto.leadId());
        Pipeline pipeline = findPipelineById(dto.pipelineId());
        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        Opportunity opportunity = buildOpportunity(dto, lead, currentStage);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    /**
     * Updates an open opportunity while enforcing that the selected stage belongs to
     * the selected pipeline. Closed opportunities cannot be changed through the
     * general update flow because closing a deal is treated as a final business
     * event.
     *
     * @param id identifier of the opportunity being updated
     * @param dto payload containing the revised opportunity state
     * @return the persisted opportunity mapped to the API response DTO
     * @throws ObjectNotFoundException if the opportunity or referenced records do not exist
     * @throws BusinessRuleException if the opportunity is already closed or the selected stage does not belong to the selected pipeline
     */
    @Transactional
    public OpportunityResponseDTO update(Long id, OpportunityRequestDTO dto) {
        Opportunity opportunity = findOpportunityById(id);

        ensureOpportunityIsOpen(opportunity);

        Lead lead = findLeadById(dto.leadId());
        Pipeline pipeline = findPipelineById(dto.pipelineId());
        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        updateOpportunityData(opportunity, dto, lead, currentStage);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    /**
     * Closes an opportunity as won or lost and enforces the rule that lost deals
     * must record a loss reason.
     *
     * @param id identifier of the opportunity being closed
     * @param dto payload indicating the closing outcome
     * @return the persisted opportunity mapped to the API response DTO
     * @throws ObjectNotFoundException if the opportunity does not exist
     * @throws BusinessRuleException if the opportunity is already closed or lacks a loss reason
     */
    @Transactional
    public OpportunityResponseDTO close(Long id, OpportunityCloseDTO dto) {
        Opportunity opportunity = findOpportunityById(id);

        ensureOpportunityIsOpen(opportunity);
        validateClosePayload(dto);
        applyClosingResult(opportunity, dto);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    /**
     * Deletes an opportunity and removes dependent tasks so no orphan follow-ups
     * remain linked to a deleted negotiation.
     *
     * @param id identifier of the opportunity to delete
     * @throws ObjectNotFoundException if the opportunity does not exist
     */
    @Transactional
    public void delete(Long id) {
        Opportunity opportunity = findOpportunityById(id);

        taskRepository.deleteByOpportunityId(opportunity.getId());
        opportunityRepository.delete(opportunity);
    }

    private Opportunity buildOpportunity(
            OpportunityRequestDTO dto,
            Lead lead,
            Stage currentStage
    ) {
        return new Opportunity(
                lead,
                dto.title(),
                dto.definitiveSolution(),
                dto.estimatedValue(),
                currentStage,
                dto.expectedCloseDate(),
                dto.notes()
        );
    }

    private void updateOpportunityData(
            Opportunity opportunity,
            OpportunityRequestDTO dto,
            Lead lead,
            Stage currentStage
    ) {
        opportunity.setLead(lead);
        opportunity.setTitle(dto.title());
        opportunity.setDefinitiveSolution(dto.definitiveSolution());
        opportunity.setEstimatedValue(dto.estimatedValue());
        opportunity.setCurrentStage(currentStage);
        opportunity.setExpectedCloseDate(dto.expectedCloseDate());
        opportunity.setNotes(dto.notes());
        opportunity.setUpdatedAt(LocalDate.now());
    }

    private Stage resolveCurrentStage(Long currentStageId, Pipeline pipeline) {
        if (currentStageId == null) {
            return resolveFirstStage(pipeline);
        }

        Stage stage = findStageById(currentStageId);
        ensureStageBelongsToPipeline(stage, pipeline);

        return stage;
    }

    private Stage resolveFirstStage(Pipeline pipeline) {
        Stage firstStage = pipeline.getFirstStage();

        if (firstStage == null) {
            throw new BusinessRuleException("O pipeline informado não possui etapas cadastradas.");
        }

        return firstStage;
    }

    private void ensureStageBelongsToPipeline(Stage stage, Pipeline pipeline) {
        if (stage.getPipeline() == null || !stage.getPipeline().getId().equals(pipeline.getId())) {
            throw new BusinessRuleException("A etapa informada não pertence ao pipeline selecionado.");
        }
    }

    private void ensureOpportunityIsOpen(Opportunity opportunity) {
        if (opportunity.getClosedAt() != null) {
            throw new BusinessRuleException("Essa oportunidade já foi fechada.");
        }
    }

    private void validateClosePayload(OpportunityCloseDTO dto) {
        if (Boolean.FALSE.equals(dto.win()) && isBlank(dto.lossReason())) {
            throw new BusinessRuleException("O motivo de perda é obrigatório quando a oportunidade é perdida.");
        }
    }

    private void applyClosingResult(Opportunity opportunity, OpportunityCloseDTO dto) {
        opportunity.setWon(dto.win());
        opportunity.setClosedAt(LocalDate.now());
        opportunity.setLossReason(resolveLossReason(dto));
        opportunity.setUpdatedAt(LocalDate.now());
    }

    private String resolveLossReason(OpportunityCloseDTO dto) {
        if (Boolean.TRUE.equals(dto.win())) {
            return null;
        }

        return dto.lossReason().trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Lead findLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Lead não encontrado. ID: " + id));
    }

    private Pipeline findPipelineById(Long id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pipeline não encontrado. ID: " + id));
    }

    private Stage findStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrada. ID: " + id));
    }

    private Opportunity findOpportunityById(Long id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada. ID: " + id));
    }
}
