package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.Solution;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpportunityResponseDTO(
        Long id,
        Long leadId,
        String title,
        Solution definitiveSolution,
        BigDecimal estimatedValue,
        Long pipelineId,
        Long currentStageId,
        String currentStageName,
        boolean won, // Adicionado para bater com o entity.isWon()
        LocalDate expectedCloseDate,
        LocalDate closeDate,
        String lossReason,
        String notes,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public static OpportunityResponseDTO daEntidade(Opportunity entity) {
        return new OpportunityResponseDTO(
                entity.getId(),
                entity.getLead() != null ? entity.getLead().getId() : null,
                entity.getTitle(),
                entity.getDefinitiveSolution(),
                entity.getEstimatedValue(),
                (entity.getCurrentStage() != null && entity.getCurrentStage().getPipeline() != null)
                        ? entity.getCurrentStage().getPipeline().getId() : null,
                entity.getCurrentStage() != null ? entity.getCurrentStage().getId() : null,
                entity.getCurrentStage() != null ? entity.getCurrentStage().getName() : null,
                entity.isWon(),
                entity.getExpectedCloseDate(),
                entity.getClosedAt(),
                entity.getLossReason(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}