package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static br.com.vendemais.support.TestReflectionUtils.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private PipelineRepository pipelineRepository;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    @Test
    void createShouldPersistOpportunityWhenDataIsValid() {
        Lead lead = lead(1L);
        Pipeline pipeline = pipeline(10L, "Vendas");
        Stage stage = stage(100L, "Contato", 1, pipeline);

        OpportunityRequestDTO dto = new OpportunityRequestDTO(
                lead.getId(),
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("1500.00"),
                pipeline.getId(),
                stage.getId(),
                LocalDate.now().plusDays(15),
                "Observacao"
        );

        when(leadRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(pipelineRepository.findById(pipeline.getId())).thenReturn(Optional.of(pipeline));
        when(stageRepository.findById(stage.getId())).thenReturn(Optional.of(stage));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> {
            Opportunity saved = invocation.getArgument(0);
            setId(saved, 999L);
            return saved;
        });

        OpportunityResponseDTO response = opportunityService.create(dto);

        assertThat(response.id()).isEqualTo(999L);
        assertThat(response.leadId()).isEqualTo(lead.getId());
        assertThat(response.pipelineId()).isEqualTo(pipeline.getId());
        assertThat(response.currentStageId()).isEqualTo(stage.getId());
        assertThat(response.title()).isEqualTo(dto.title());
        assertThat(response.definitiveSolution()).isEqualTo(dto.definitiveSolution());

        verify(leadRepository).save(lead);
        verify(opportunityRepository).save(any(Opportunity.class));
    }

    @Test
    void createShouldRejectStageFromAnotherPipeline() {
        Lead lead = lead(1L);
        Pipeline selectedPipeline = pipeline(10L, "Vendas");
        Pipeline otherPipeline = pipeline(20L, "Outro");
        Stage foreignStage = stage(100L, "Qualificacao", 2, otherPipeline);

        OpportunityRequestDTO dto = new OpportunityRequestDTO(
                lead.getId(),
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("2000.00"),
                selectedPipeline.getId(),
                foreignStage.getId(),
                LocalDate.now().plusDays(10),
                null
        );

        when(leadRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(pipelineRepository.findById(selectedPipeline.getId())).thenReturn(Optional.of(selectedPipeline));
        when(stageRepository.findById(foreignStage.getId())).thenReturn(Optional.of(foreignStage));

        assertThatThrownBy(() -> opportunityService.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("pipeline");

        verify(opportunityRepository, never()).save(any(Opportunity.class));
    }

    @Test
    void createShouldFallbackToFirstStageWhenCurrentStageIsNull() {
        Lead lead = lead(1L);
        Pipeline pipeline = pipeline(10L, "Vendas");
        Stage stageLate = stage(101L, "Proposta", 3, pipeline);
        Stage stageFirst = stage(102L, "Contato", 1, pipeline);
        pipeline.addStage(stageLate);
        pipeline.addStage(stageFirst);

        OpportunityRequestDTO dto = new OpportunityRequestDTO(
                lead.getId(),
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("3200.00"),
                pipeline.getId(),
                null,
                LocalDate.now().plusDays(20),
                "Sem etapa informada"
        );

        when(leadRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(pipelineRepository.findById(pipeline.getId())).thenReturn(Optional.of(pipeline));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> {
            Opportunity saved = invocation.getArgument(0);
            setId(saved, 500L);
            return saved;
        });

        OpportunityResponseDTO response = opportunityService.create(dto);

        assertThat(response.currentStageId()).isEqualTo(stageFirst.getId());
        assertThat(response.currentStageName()).isEqualTo(stageFirst.getName());

        verifyNoInteractions(stageRepository);
    }

    @Test
    void hasOpenOpportunitiesShouldReturnRepositoryResult() {
        when(opportunityRepository.existsByLeadIdAndClosedAtIsNull(7L)).thenReturn(true);

        boolean hasOpenOpportunities = opportunityService.hasOpenOpportunities(7L);

        assertThat(hasOpenOpportunities).isTrue();
        verify(opportunityRepository).existsByLeadIdAndClosedAtIsNull(7L);
    }

    @Test
    void hasOpenOpportunitiesShouldRejectNullLeadId() {
        assertThatThrownBy(() -> opportunityService.hasOpenOpportunities(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lead");

        verify(opportunityRepository, never()).existsByLeadIdAndClosedAtIsNull(any());
    }

    private Lead lead(Long id) {
        Lead lead = new Lead(
                "Lead Teste",
                "11999999999",
                "lead@example.com",
                PersonType.COMPANY,
                "Empresa",
                Solution.COWORKING,
                LeadSource.SITE,
                EntryMethod.MANUAL,
                "Observacao"
        );
        setId(lead, id);
        return lead;
    }

    private Pipeline pipeline(Long id, String title) {
        Pipeline pipeline = new Pipeline(title);
        setId(pipeline, id);
        return pipeline;
    }

    private Stage stage(Long id, String name, Integer position, Pipeline pipeline) {
        Stage stage = new Stage(name, name.toUpperCase(), position, pipeline);
        setId(stage, id);
        return stage;
    }
}
