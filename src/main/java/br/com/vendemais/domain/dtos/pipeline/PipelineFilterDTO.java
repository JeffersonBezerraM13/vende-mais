package br.com.vendemais.domain.dtos.pipeline;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Represents optional filtering criteria used to search pipeline listings.
 */
@Schema(name = "PipelineFilterDTO", description = "Parametros opcionais para filtragem da listagem de pipelines.")
public record PipelineFilterDTO(
        @Size(max = 120, message = "O termo de busca deve ter no máximo 120 caracteres")
        @Schema(
                description = "Termo usado para buscar pipelines pelo titulo.",
                example = "Funil Comercial"
        )
        String search
) {
}