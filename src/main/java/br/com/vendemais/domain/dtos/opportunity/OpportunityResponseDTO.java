package br.com.vendemais.domain.dtos.opportunity;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.InterestSolution;
import java.time.LocalDate;

public record OpportunityResponseDTO(
        Long id,
        Long leadId,
        String title,
        InterestSolution definitiveSolution,
        Float estimatedValue,
        Long pipelineId,
        LocalDate expectedCloseDate,
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
                entidade.getExpectedCloseDate(),
                entidade.getLossReason(),
                entidade.getNotes(),
                entidade.getCreatedAt(),
                entidade.getUpdatedAt()
        );
    }
}