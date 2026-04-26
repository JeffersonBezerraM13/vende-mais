package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.enums.filter.OpportunityStatusFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Represents optional filtering criteria used to search and narrow opportunity listings.
 */
@Schema(name = "OpportunityFilterDTO", description = "Parâmetros opcionais para filtragem da listagem de oportunidades.")
public record OpportunityFilterDTO(
        @Size(max = 120, message = "O termo de busca deve ter no máximo 120 caracteres")
        @Schema(
                description = "Termo usado para buscar oportunidades por lead, título, funil, etapa ou observações.",
                example = "Ana"
        )
        String search,

        @Schema(
                description = "Status virtual da oportunidade, calculado por won e closedAt.",
                example = "OPEN"
        )
        OpportunityStatusFilter status,

        @Schema(
                description = "Filtra oportunidades pelo ID do funil.",
                example = "1"
        )
        Long pipelineId
) {
}
