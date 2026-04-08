package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.vendemais.support.TestReflectionUtils.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private PipelineRepository pipelineRepository;

    @InjectMocks
    private StageService stageService;

    @Test
    void createStageShouldAssociatePipelineAndPersistStage() {
        Pipeline pipeline = new Pipeline("Pipeline Comercial");
        setId(pipeline, 10L);
        StageRequestDTO dto = new StageRequestDTO("Contato", "CONTATO", 1, pipeline.getId());

        when(pipelineRepository.findById(pipeline.getId())).thenReturn(Optional.of(pipeline));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
            Stage saved = invocation.getArgument(0);
            setId(saved, 90L);
            return saved;
        });

        StageResponseDTO response = stageService.createStage(dto);

        assertThat(response.id()).isEqualTo(90L);
        assertThat(response.pipelineId()).isEqualTo(pipeline.getId());
        assertThat(response.name()).isEqualTo(dto.name());
        assertThat(pipeline.getStages()).hasSize(1);
        assertThat(pipeline.getFirstStage().getName()).isEqualTo(dto.name());
    }

    @Test
    void createStageShouldRejectMissingPipeline() {
        StageRequestDTO dto = new StageRequestDTO("Contato", "CONTATO", 1, 10L);
        when(pipelineRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stageService.createStage(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Funil");
    }

    @Test
    void updateStageShouldRejectStageOutsidePipeline() {
        StageRequestDTO dto = new StageRequestDTO("Contato", "CONTATO", 1, 10L);
        when(stageRepository.findByIdAndPipelineId(99L, dto.pipelineId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stageService.updateStage(99L, dto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("funil");
    }
}
