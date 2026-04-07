package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.enums.Solution;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record OpportunityRequestDTO(
        @NotNull(message = "O ID do Lead é obrigatório")
        Long leadId,

        @NotBlank(message = "O título não pode ser vazio")
        String title,

        Solution definitiveSolution,
        Float estimatedValue,

        Long pipelineId,
        LocalDate expectedCloseDate,
        String notes
) {}