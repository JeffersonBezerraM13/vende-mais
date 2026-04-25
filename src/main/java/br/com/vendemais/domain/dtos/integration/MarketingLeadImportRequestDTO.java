package br.com.vendemais.domain.dtos.integration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Captures the amount of external marketing leads to import into the CRM.
 */
@Schema(name = "MarketingLeadImportRequestDTO", description = "Payload para importar leads simulados de uma API externa de marketing.")
public record MarketingLeadImportRequestDTO(
        @NotNull(message = "A quantidade de leads é obrigatória")
        @Min(value = 1, message = "A quantidade mínima para importação é 1")
        @Max(value = 20, message = "A quantidade máxima para importação é 20")
        @Schema(description = "Quantidade de leads que devem ser importados da API externa.", example = "5")
        Integer quantity
) {
}