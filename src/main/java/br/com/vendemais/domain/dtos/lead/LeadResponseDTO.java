package br.com.vendemais.domain.dtos.lead;

import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.enums.*;
import java.time.LocalDate;

public record LeadResponseDTO(
        Long id,
        String name,
        String phone,
        String email,
        PersonType personType,
        String companyName,
        Solution interestSoluction,
        LeadSource leadSource,
        EntryMethod entryMethod,
        String notes,
        LocalDate createdAt,
        LocalDate updatedAt
) {
    public static LeadResponseDTO daEntidade(Lead entidade) {
        return new LeadResponseDTO(
                entidade.getId(),
                entidade.getName(),
                entidade.getPhone(),
                entidade.getEmail(),
                entidade.getPersonType(),
                entidade.getCompanyName(),
                entidade.getInterestSolution(),
                entidade.getLeadSource(),
                entidade.getEntryMethod(),
                entidade.getNotes(),
                entidade.getCreatedAt(),
                entidade.getUpdatedAt()
        );
    }
}