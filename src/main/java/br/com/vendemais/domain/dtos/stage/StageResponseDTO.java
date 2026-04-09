package br.com.vendemais.domain.dtos.stage;

import br.com.vendemais.domain.entity.Stage;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents an ordered pipeline stage returned to clients configuring or
 * visualizing funnel structure.
 */
@Schema(name = "StageResponseDTO", description = "Representacao de etapa retornada pela API.")
public record StageResponseDTO(
        @Schema(example = "3")
        Long id,
        @Schema(example = "Qualificacao")
        String name,
        @Schema(example = "QUALIFICACAO")
        String code,
        @Schema(example = "3")
        Integer position,
        @Schema(example = "1")
        Long pipelineId
) {
    public static StageResponseDTO daEntidade(Stage entidade) {
        return new StageResponseDTO(
                entidade.getId(),
                entidade.getName(),
                entidade.getCode(),
                entidade.getPosition(),
                entidade.getPipeline() != null ? entidade.getPipeline().getId() : null
        );
    }
}
