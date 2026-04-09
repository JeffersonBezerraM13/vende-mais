package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.enums.Solution;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Captures the payload required to register or revise an opportunity inside a
 * pipeline.
 */
@Schema(name = "OpportunityRequestDTO", description = "Payload para criacao ou atualizacao de oportunidades.")
public record OpportunityRequestDTO(
        @NotNull(message = "O ID do Lead é obrigatório")
        @Schema(example = "2")
        Long leadId,

        @NotBlank(message = "O título da oportunidade não pode ser vazio")
        @Schema(example = "Coworking - Bob")
        String title,

        @NotNull(message = "A solução definitiva é obrigatória")
        @Schema(example = "COWORKING")
        Solution definitiveSolution,

        @Schema(example = "4500.00")
        BigDecimal estimatedValue,

        @NotNull(message = "O ID do Pipeline é obrigatório")
        @Schema(example = "1")
        Long pipelineId,

        @Schema(example = "4")
        Long currentStageId,

        @Schema(example = "2026-04-24")
        LocalDate expectedCloseDate,

        @Schema(example = "Blue Corp precisa de 4 posicoes fixas e sala de reuniao duas vezes por semana.")
        String notes
) {}
