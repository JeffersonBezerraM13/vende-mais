package br.com.vendemais.domain.dtos.task;

import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * Represents a scheduled CRM task returned to API consumers.
 */
@Schema(name = "TaskResponseDTO", description = "Representação de tarefa retornada pela API.")
public record TaskResponseDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "5")
        Long userId,
        @Schema(example = "Ligar para Bob Blue")
        String title,
        @Schema(example = "Validar a urgência da Blue Corp para contratar Coworking.")
        String description,
        @Schema(example = "PENDING")
        TaskStatus taskStatus,
        @Schema(example = "2026-04-12")
        LocalDate dueDate,
        @Schema(example = "2")
        Long leadId,
        @Schema(example = "null", nullable = true)
        Long opportunityId,
        @Schema(example = "2026-04-09")
        LocalDate createdAt,
        @Schema(example = "2026-04-10")
        LocalDate updatedAt
) {
    public static TaskResponseDTO daEntidade(Task entidade) {
        return new TaskResponseDTO(
                entidade.getId(),
                entidade.getUser() != null ? entidade.getUser().getId() : null,
                entidade.getTitle(),
                entidade.getDescription(),
                entidade.getStatus(),
                entidade.getDueDate(),
                entidade.getLead() != null ? entidade.getLead().getId() : null,
                entidade.getOpportunity() != null ? entidade.getOpportunity().getId() : null,
                entidade.getCreatedAt(),
                entidade.getUpdatedAt()
        );
    }
}
