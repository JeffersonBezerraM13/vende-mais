package br.com.vendemais.domain.dtos.lead;

import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Represents optional filtering criteria used to search and narrow lead listings.
 */
@Schema(name = "LeadFilterDTO", description = "Parametros opcionais para filtragem da listagem de leads.")
public record LeadFilterDTO(
        @Size(max = 120, message = "O termo de busca deve ter no máximo 120 caracteres")
        @Schema(
                description = "Termo usado para buscar leads por nome, email, telefone ou nome da empresa.",
                example = "Bob"
        )
        String search,

        @Schema(
                description = "Filtra leads pelo tipo de pessoa.",
                example = "COMPANY"
        )
        PersonType personType,

        @Schema(
                description = "Filtra leads pela origem do contato.",
                example = "PHONE_CALL"
        )
        LeadSource leadSource
) {
}