package br.com.vendemais.domain.dtos.task;

import br.com.vendemais.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Captures the information required to schedule or update a follow-up task
 * linked to a lead or opportunity.
 */
@Schema(name = "TaskRequestDTO", description = "Payload para criação ou atualização de tarefas.")
public record TaskRequestDTO(

        @NotBlank(message = "Uma task deve ter um nome")
        @Schema(example = "Ligar para Bob Blue")
        String title,

        @Schema(example = "Validar a urgência da Blue Corp para contratar Coworking.")
        String description,
        @Schema(example = "PENDING")
        TaskStatus taskStatus,

        @NotNull(message = "Data de vencimento não pode ser vazia")
        @Schema(example = "2026-04-12")
        LocalDate dueDate,

        @Schema(example = "2")
        Long leadId,
        @Schema(example = "null", nullable = true)
        Long opportunityId
) {}
