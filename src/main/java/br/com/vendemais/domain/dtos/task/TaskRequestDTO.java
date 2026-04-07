package br.com.vendemais.domain.dtos.task;

import br.com.vendemais.domain.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskRequestDTO(
        @NotBlank(message = "Uma task deve ter um nome")
        String title,

        String description,
        Status status,

        @NotNull(message = "Data de vencimento não pode ser vazia")
        LocalDate dueDate,

        Long leadId,
        Long opportunityId
) {}