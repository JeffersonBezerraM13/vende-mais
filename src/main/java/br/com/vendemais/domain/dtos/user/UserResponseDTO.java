package br.com.vendemais.domain.dtos.user;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * Represents a CRM user account as exposed by administrative and profile
 * endpoints.
 */
@Schema(name = "UserResponseDTO", description = "Representacao de usuario retornada pela API.")
public record UserResponseDTO(
        @Schema(example = "3")
        Long id,
        @Schema(example = "Nikola Tesla")
        String name,
        @Schema(example = "tesla@gmail.com")
        String email,
        @Schema(example = "[\"USER\"]")
        Set<Role> roles
) {
    public static UserResponseDTO daEntidade(User entidade) {
        return new UserResponseDTO(
                entidade.getId(),
                entidade.getName(),
                entidade.getEmail(),
                entidade.getRoles()
        );
    }
}
