package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.StageType;
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
        StageType currentStageType,
        LocalDate expectedCloseDate,
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
                entity.getPipeline() != null ? entity.getPipeline().getId() : null,
                entity.getCurrentStage() != null ? entity.getCurrentStage().getId() : null,
                entity.getCurrentStage() != null ? entity.getCurrentStage().getName() : null,
                entity.getCurrentStage() != null ? entity.getCurrentStage().getType() : null,
                entity.getExpectedCloseDate(),
                entity.getLossReason(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}