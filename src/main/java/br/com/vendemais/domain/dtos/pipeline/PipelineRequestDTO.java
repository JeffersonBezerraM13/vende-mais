package br.com.vendemais.domain.dtos.pipeline;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Carries the minimal information required to define or rename a sales
 * pipeline.
 */
@Schema(name = "PipelineRequestDTO", description = "Payload para criação ou atualização de pipelines.")
public record PipelineRequestDTO(
        @NotBlank(message = "O título do pipeline não pode ser vazio")
        @Schema(example = "Funil Comercial")
        String title
) {}
