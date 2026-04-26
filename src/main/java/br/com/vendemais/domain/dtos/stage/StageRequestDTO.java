package br.com.vendemais.domain.dtos.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Captures the data required to create or update a stage within a sales
 * pipeline.
 */
@Schema(name = "StageRequestDTO", description = "Payload para criação ou atualização de etapas.")
public record StageRequestDTO(
        @NotBlank(message = "O nome do estágio não pode ser vazio")
        @Schema(example = "Qualificação")
        String name,

        @NotBlank(message = "O código do estágio não pode ser vazio")
        @Schema(example = "QUALIFICACAO")
        String code,

        @NotNull(message = "A posição do estágio é obrigatória")
        @Schema(example = "3")
        Integer position,

        @NotNull(message = "O id do funíl precisa ser informado")
        @Schema(example = "1")
        Long pipelineId
) {}
