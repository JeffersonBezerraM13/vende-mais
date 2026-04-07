package br.com.vendemais.domain.dtos.task;

import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.enums.Status;
import java.time.LocalDate;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Status status,
        LocalDate dueDate,
        Long leadId,
        Long opportunityId,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public static TaskResponseDTO daEntidade(Task entidade) {
        return new TaskResponseDTO(
                entidade.getId(),
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