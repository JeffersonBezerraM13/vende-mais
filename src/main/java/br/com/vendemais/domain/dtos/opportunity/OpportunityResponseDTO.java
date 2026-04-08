package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.OpportunityStatus;
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
        LocalDate expectedCloseDate,
        OpportunityStatus status,
        String lossReason,
        String notes,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public static OpportunityResponseDTO daEntidade(Opportunity entidade) {
        return new OpportunityResponseDTO(
                entidade.getId(),
                entidade.getLead() != null ? entidade.getLead().getId() : null,
                entidade.getTitle(),
                entidade.getDefinitiveSolution(),
                entidade.getEstimatedValue(),
                entidade.getPipeline() != null ? entidade.getPipeline().getId() : null,
                entidade.getCurrentStage() != null ? entidade.getCurrentStage().getId() : null,
                entidade.getExpectedCloseDate(),
                entidade.getStatus(),
                entidade.getLossReason(),
                entidade.getNotes(),
                entidade.getCreatedAt(),
                entidade.getUpdatedAt()
        );
    }
}