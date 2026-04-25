package br.com.vendemais.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Carries the credentials submitted to the public login endpoint in exchange for
 * a JWT.
 */
@Schema(name = "CredentialsDTO", description = "Credenciais utilizadas no endpoint publico de login.")
public record CredentialsDTO(
        @Schema(example = "einstein@gmail.com")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @Schema(example = "123456")
        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
