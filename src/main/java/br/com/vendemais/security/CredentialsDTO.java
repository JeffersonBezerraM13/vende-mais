package br.com.vendemais.security;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Carries the credentials submitted to the public login endpoint in exchange for
 * a JWT.
 */
@Schema(name = "CredentialsDTO", description = "Credenciais utilizadas no endpoint publico de login.")
public record CredentialsDTO(
        @Schema(example = "einstein@gmail.com")
        String email,

        @Schema(example = "123456")
        String password
) {
}
