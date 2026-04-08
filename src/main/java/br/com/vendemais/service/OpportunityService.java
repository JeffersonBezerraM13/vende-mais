package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.LeadStatus;
import br.com.vendemais.domain.enums.StageType;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final LeadRepository leadRepository;
    private final PipelineRepository pipelineRepository;
    private final StageRepository stageRepository;

    public OpportunityService(
            OpportunityRepository opportunityRepository,
            LeadRepository leadRepository,
            PipelineRepository pipelineRepository,
            StageRepository stageRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.leadRepository = leadRepository;
        this.pipelineRepository = pipelineRepository;
        this.stageRepository = stageRepository;
    }

    public Page<OpportunityResponseDTO> findAll(Pageable pageable) {
        Page<Opportunity> paginaDeOpportunitys = opportunityRepository.findAll(pageable);

        return paginaDeOpportunitys.map(OpportunityResponseDTO::daEntidade);
    }

    public OpportunityResponseDTO findById(Long id) {
        Opportunity opportunity = findOpportunityById(id).orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));
        return OpportunityResponseDTO.daEntidade(opportunity);
    }

    private Optional<Opportunity> findOpportunityById(Long id) {
        return opportunityRepository.findById(id);
    }

    public boolean hasOpenOpportunities(Long leadId) {
        if (leadId == null) {
            throw new IllegalArgumentException("O ID do Lead não pode ser nulo.");
        }

        return opportunityRepository.existsByLeadIdAndStageType(leadId, StageType.OPEN);
    }

    @Transactional
    public OpportunityResponseDTO create(OpportunityRequestDTO dto) {
        Lead lead = leadRepository.findById(dto.leadId())
                .orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        if (lead.getLeadStatus() == LeadStatus.DISQUALIFIED) {
            throw new DataIntegrityViolationException("Esse lead está desqualificado");
        }

        Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        validateLossReason(currentStage, dto.lossReason());

        Opportunity opportunity = new Opportunity(
                lead,
                dto.title(),
                dto.definitiveSolution(),
                dto.estimatedValue(),
                pipeline,
                currentStage,
                dto.expectedCloseDate(),
                currentStage.getType() == StageType.LOST ? dto.lossReason() : null,
                dto.notes()
        );

        lead.setLeadStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    @Transactional
    public OpportunityResponseDTO update(Long id, OpportunityRequestDTO dto) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Opportunity não encontrada"));

        Lead lead = leadRepository.findById(dto.leadId())
                .orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Stage currentStage = resolveCurrentStage(dto.currentStageId(), pipeline);

        validateLossReason(currentStage, dto.lossReason());

        opportunity.setLead(lead);
        opportunity.setTitle(dto.title());
        opportunity.setDefinitiveSolution(dto.definitiveSolution());
        opportunity.setEstimatedValue(dto.estimatedValue());
        opportunity.setPipeline(pipeline);
        opportunity.setCurrentStage(currentStage);
        opportunity.setExpectedCloseDate(dto.expectedCloseDate());
        opportunity.setLossReason(currentStage.getType() == StageType.LOST ? dto.lossReason() : null);
        opportunity.setNotes(dto.notes());
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

    public void delete(Long id){
        Opportunity opportunity = findOpportunityById(id).orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));
        opportunityRepository.delete(opportunity);
    }

    private void validateLossReason(Stage currentStage, String lossReason) {
        if (currentStage.getType() == StageType.LOST
                && (lossReason == null || lossReason.isBlank())) {
            throw new DataIntegrityViolationException("O motivo de perda é obrigatório para oportunidades perdidas.");
        }
    }
}