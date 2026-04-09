package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.opportunity.OpportunityCloseDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.repository.*;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Orchestrates opportunity lifecycle rules, including pipeline assignment, stage
 * validation, and win/loss closing logic.
 */
@Service
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
     * Retrieves opportunities in pages so pipeline and portfolio screens can be
     * rendered efficiently.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing opportunity projections mapped to response DTOs
     */
    public Page<OpportunityResponseDTO> findAll(Pageable pageable) {
        Page<Opportunity> paginaDeOpportunitys = opportunityRepository.findAll(pageable);

        return paginaDeOpportunitys.map(OpportunityResponseDTO::daEntidade);
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
        Opportunity opportunity = findOpportunityById(id).orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));
        return OpportunityResponseDTO.daEntidade(opportunity);
    }

    private Optional<Opportunity> findOpportunityById(Long id) {
        return opportunityRepository.findById(id);
    }

    /**
     * Checks whether a lead already has open negotiations before another
     * opportunity is created for it.
     *
     * @param leadId identifier of the lead being inspected
     * @return {@code true} when the lead has at least one opportunity without a close date
     * @throws IllegalArgumentException if {@code leadId} is {@code null}
     */
    public boolean hasOpenOpportunities(Long leadId) {
        if (leadId == null) {
            throw new IllegalArgumentException("O ID do Lead não pode ser nulo.");
        }

        return opportunityRepository.existsByLeadIdAndClosedAtIsNull(leadId);
    }

    /**
     * Creates a new opportunity and validates that the referenced lead, pipeline,
     * and stage form a consistent CRM context.
     *
     * @param dto payload describing the opportunity to persist
     * @return the persisted opportunity mapped to the API response DTO
     * @throws DataIntegrityViolationException if referenced lead, pipeline, or stage data is invalid
     */
    @Transactional
    public OpportunityResponseDTO create(OpportunityRequestDTO dto) {
        Lead lead = leadRepository.findById(dto.leadId())
                .orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        Opportunity opportunity = new Opportunity(
                lead,
                dto.title(),
                dto.definitiveSolution(),
                dto.estimatedValue(),
                currentStage,
                dto.expectedCloseDate(),
                dto.notes()
        );

        leadRepository.save(lead);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    /**
     * Updates an opportunity while enforcing that the selected stage belongs to
     * the selected pipeline.
     *
     * @param id identifier of the opportunity being updated
     * @param dto payload containing the revised opportunity state
     * @return the persisted opportunity mapped to the API response DTO
     * @throws ObjectNotFoundException if the opportunity does not exist
     * @throws DataIntegrityViolationException if referenced lead, pipeline, or stage data is invalid
     */
    @Transactional
    public OpportunityResponseDTO update(Long id, OpportunityRequestDTO dto) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Opportunity não encontrada"));

        Lead lead = leadRepository.findById(dto.leadId())
                .orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        opportunity.setLead(lead);
        opportunity.setTitle(dto.title());
        opportunity.setDefinitiveSolution(dto.definitiveSolution());
        opportunity.setEstimatedValue(dto.estimatedValue());
        opportunity.setCurrentStage(currentStage);
        opportunity.setExpectedCloseDate(dto.expectedCloseDate());
        opportunity.setNotes(dto.notes());
        opportunity.setUpdatedAt(LocalDate.now());

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
     * @throws DataIntegrityViolationException if the opportunity is already closed or lacks a loss reason
     */
    @Transactional
    public OpportunityResponseDTO close(Long id, OpportunityCloseDTO dto) {
        Opportunity opportunity = findOpportunityById(id).orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));

        if (opportunity.getClosedAt() != null) {
            throw new DataIntegrityViolationException("Essa oportunidade já foi fechada.");
        }

        if (Boolean.FALSE.equals(dto.win()) && (dto.lossReason() == null || dto.lossReason().isBlank())) {
            throw new DataIntegrityViolationException("O motivo de perda é obrigatório quando a oportunidade é perdida.");
        }

        opportunity.setWon(dto.win());
        opportunity.setClosedAt(LocalDate.now());
        opportunity.setLossReason(Boolean.TRUE.equals(dto.win()) ? null : dto.lossReason().trim());
        opportunity.setUpdatedAt(LocalDate.now());

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    private Stage resolveCurrentStage(Long currentStageId, Pipeline pipeline) {
        if (currentStageId == null) {
            Stage firstStage = pipeline.getFirstStage();

            if (firstStage == null) {
                throw new DataIntegrityViolationException("O pipeline informado não possui etapas cadastradas.");
            }

            return firstStage;
        }

        Stage stage = stageRepository.findById(currentStageId)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrada"));

        if (!stage.getPipeline().getId().equals(pipeline.getId())) {
            throw new DataIntegrityViolationException("A etapa informada não pertence ao pipeline selecionado.");
        }

        return stage;
    }

    /**
     * Deletes an opportunity and removes dependent tasks so no orphan follow-ups
     * remain linked to a deleted negotiation.
     *
     * @param id identifier of the opportunity to delete
     * @throws ObjectNotFoundException if the opportunity does not exist
     */
    @Transactional
    public void delete(Long id){
        Opportunity opportunity = findOpportunityById(id).orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));

        taskRepository.deleteByOpportunityId(opportunity.getId());

        opportunityRepository.delete(opportunity);
    }
}
