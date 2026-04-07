package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
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
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final LeadRepository leadRepository;
    private final PipelineRepository pipelineRepository;
    private final StageRepository stageRepository;

    public OpportunityService(OpportunityRepository opportunityRepository, LeadRepository leadRepository, PipelineRepository pipelineRepository, StageRepository stageRepository) {
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
        Opportunity opportunity = findOpportunityById(id);
        return OpportunityResponseDTO.daEntidade(opportunity);
    }

    public boolean hasOpenOpportunities(Long leadId) {
        if (leadId == null) {
            throw new IllegalArgumentException("O ID do Lead não pode ser nulo.");
        }

        // Retorna true se achar pelo menos uma negociação em andamento
        return opportunityRepository.existsByLeadIdAndCurrentStage_FinalStageFalse(leadId);
    }

    public OpportunityResponseDTO create(OpportunityRequestDTO opportunityRequestDTO) {
        Lead lead = leadRepository.findById(opportunityRequestDTO.leadId()).orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        Pipeline pipeline = pipelineRepository.findById(opportunityRequestDTO.pipelineId()).orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Opportunity opportunity = getOpportunity(opportunityRequestDTO, lead, pipeline);

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    private Opportunity getOpportunity(OpportunityRequestDTO opportunityRequestDTO, Lead lead, Pipeline pipeline) {
        Opportunity opportunity = new Opportunity(
                lead,
                opportunityRequestDTO.title(),
                opportunityRequestDTO.definitiveSolution(),
                opportunityRequestDTO.estimatedValue(),
                pipeline,
                null,
                opportunityRequestDTO.expectedCloseDate(),
                opportunityRequestDTO.lossReason(),
                opportunityRequestDTO.notes()
        );
        if(opportunityRequestDTO.currentStageId() == null){
            opportunity.setCurrentStage(opportunity.getPipeline().getFistStage());
        } else {
            Stage stage = stageRepository.findById(opportunityRequestDTO.currentStageId())
                    .orElseThrow(() -> new DataIntegrityViolationException("Etapa não encontrada"));
            opportunity.setCurrentStage(stage);
        }
        return opportunity;
    }

    public OpportunityResponseDTO update(Long id, OpportunityRequestDTO opportunityRequestDTO) {
        Lead lead = leadRepository.findById(opportunityRequestDTO.leadId()).orElseThrow(() -> new DataIntegrityViolationException("Lead não existente"));

        Pipeline pipeline = pipelineRepository.findById(opportunityRequestDTO.pipelineId()).orElseThrow(() -> new DataIntegrityViolationException("Funil não existe"));

        Opportunity opportunity = findOpportunityById(id);

        opportunity.setLead(lead);
        opportunity.setTitle(opportunityRequestDTO.title());
        opportunity.setDefinitiveSolution(opportunityRequestDTO.definitiveSolution());
        opportunity.setEstimatedValue(opportunityRequestDTO.estimatedValue());
        opportunity.setPipeline(pipeline);
        opportunity.setExpectedCloseDate(opportunityRequestDTO.expectedCloseDate());
        opportunity.setLossReason(opportunityRequestDTO.lossReason());
        opportunity.setNotes(opportunityRequestDTO.notes());
        opportunity.setUpdatedAt(LocalDate.now());

        if(opportunityRequestDTO.currentStageId() != null) {
            Stage stage = stageRepository.findById(opportunityRequestDTO.currentStageId())
                    .orElseThrow(() -> new DataIntegrityViolationException("Etapa não encontrada"));
            opportunity.setCurrentStage(stage);
        }

        return OpportunityResponseDTO.daEntidade(opportunityRepository.save(opportunity));
    }

    public void delete(Long id){
        Opportunity opportunity = findOpportunityById(id);
        opportunityRepository.delete(opportunity);
    }

    private Opportunity findOpportunityById(Long id) {
        return opportunityRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Opportunity não encontrado. ID:" +id));
    }
}
