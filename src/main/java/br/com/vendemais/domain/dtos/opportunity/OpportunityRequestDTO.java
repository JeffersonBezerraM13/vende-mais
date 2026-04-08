package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.enums.Solution;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpportunityRequestDTO(
        @NotNull(message = "O ID do Lead é obrigatório")
        Long leadId,

        @NotBlank(message = "O título da oportunidade não pode ser vazio")
        String title,

        @NotNull(message = "A solução definitiva é obrigatória")
        Solution definitiveSolution,

        BigDecimal estimatedValue,

        @NotNull(message = "O ID do Pipeline é obrigatório")
        Long pipelineId,

        Long currentStageId,

        LocalDate expectedCloseDate,

        String notes
) {}