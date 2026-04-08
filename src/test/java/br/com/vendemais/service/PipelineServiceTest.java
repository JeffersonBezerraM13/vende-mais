package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PipelineServiceTest {

    @Mock
    private PipelineRepository pipelineRepository;

    @InjectMocks
    private PipelineService pipelineService;

    @Test
    void createShouldPersistPipelineWhenTitleIsUnique() {
        PipelineRequestDTO dto = new PipelineRequestDTO("Pipeline Comercial");

        when(pipelineRepository.existsByTitle(dto.title())).thenReturn(false);
        when(pipelineRepository.save(any(Pipeline.class))).thenAnswer(invocation -> {
            Pipeline saved = invocation.getArgument(0);
            setId(saved, 70L);
            return saved;
        });

        PipelineResponseDTO response = pipelineService.create(dto);

        assertThat(response.id()).isEqualTo(70L);
        assertThat(response.title()).isEqualTo(dto.title());
    }

    @Test
    void createShouldRejectDuplicateTitle() {
        PipelineRequestDTO dto = new PipelineRequestDTO("Pipeline Comercial");
        when(pipelineRepository.existsByTitle(dto.title())).thenReturn(true);

        assertThatThrownBy(() -> pipelineService.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Pipeline");
    }

    @Test
    void deleteShouldTranslateRepositoryIntegrityViolation() {
        Pipeline pipeline = new Pipeline("Pipeline Comercial");
        setId(pipeline, 10L);

        when(pipelineRepository.findById(10L)).thenReturn(Optional.of(pipeline));
        doThrow(new org.springframework.dao.DataIntegrityViolationException("constraint"))
                .when(pipelineRepository)
                .delete(pipeline);

        assertThatThrownBy(() -> pipelineService.delete(10L))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("funil");
    }
}
