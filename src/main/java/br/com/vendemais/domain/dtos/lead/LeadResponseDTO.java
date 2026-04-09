package br.com.vendemais.domain.dtos.lead;

import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * Summarizes lead data returned to clients when prospect information is queried
 * from the CRM.
 */
@Schema(name = "LeadResponseDTO", description = "Representacao de lead retornada pela API.")
public record LeadResponseDTO(
        @Schema(example = "2")
        Long id,
        @Schema(example = "Bob Blue")
        String name,
        @Schema(example = "83912345678")
        String phone,
        @Schema(example = "bob@gmail.com")
        String email,
        @Schema(example = "COMPANY")
        PersonType personType,
        @Schema(example = "Blue Corp")
        String companyName,
        @Schema(example = "COWORKING")
        Solution interestSoluction,
        @Schema(example = "PHONE_CALL")
        LeadSource leadSource,
        @Schema(example = "MANUAL")
        EntryMethod entryMethod,
        @Schema(example = "Busca 4 posicoes de coworking e sala de reuniao para atender clientes presencialmente.")
        String notes,
        @Schema(example = "2026-04-09")
        LocalDate createdAt,
        @Schema(example = "2026-04-10")
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
