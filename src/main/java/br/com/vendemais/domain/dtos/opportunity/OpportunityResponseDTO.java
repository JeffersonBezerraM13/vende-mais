package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.Solution;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents the pipeline, stage, and commercial data returned for a tracked
 * opportunity.
 */
@Schema(name = "OpportunityResponseDTO", description = "Representacao de oportunidade retornada pela API.")
public record OpportunityResponseDTO(
        @Schema(example = "2")
        Long id,
        @Schema(example = "2")
        Long leadId,
        @Schema(example = "Coworking - Bob")
        String title,
        @Schema(example = "COWORKING")
        Solution definitiveSolution,
        @Schema(example = "4500.00")
        BigDecimal estimatedValue,
        @Schema(example = "1")
        Long pipelineId,
        @Schema(example = "4")
        Long currentStageId,
        @Schema(example = "Proposta enviada")
        String currentStageName,
        @Schema(example = "false")
        boolean won, // Adicionado para bater com o entity.isWon()
        @Schema(example = "2026-04-24")
        LocalDate expectedCloseDate,
        @Schema(example = "2026-04-25")
        LocalDate closeDate,
        @Schema(example = "Blue Corp adiou a contratacao para o proximo trimestre.")
        String lossReason,
        @Schema(example = "Blue Corp precisa de 4 posicoes fixas e sala de reuniao duas vezes por semana.")
        String notes,
        @Schema(example = "2026-04-09")
        LocalDate createdAt,
        @Schema(example = "2026-04-25")
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
