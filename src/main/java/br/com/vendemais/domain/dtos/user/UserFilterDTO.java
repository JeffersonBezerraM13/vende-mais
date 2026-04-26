package br.com.vendemais.domain.dtos.user;

import br.com.vendemais.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Represents optional filtering criteria used to search user listings.
 */
@Schema(name = "UserFilterDTO", description = "Parâmetros opcionais para filtragem da listagem de usuários.")
public record UserFilterDTO(
        @Size(max = 120, message = "O termo de busca deve ter no máximo 120 caracteres")
        @Schema(
                description = "Termo usado para buscar usuários por nome ou email.",
                example = "Albert"
        )
        String search,

        @Schema(
                description = "Filtra usuários por permissão.",
                example = "ADMIN"
        )
        Role role
) {
}
