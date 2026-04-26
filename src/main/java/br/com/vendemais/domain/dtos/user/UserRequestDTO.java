package br.com.vendemais.domain.dtos.user;

import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Captures the data required to provision or update a CRM user account.
 */
@Schema(name = "UserRequestDTO", description = "Payload para criação ou atualização de usuários.")
public record UserRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        @Schema(example = "Marie Curie")
        String name,

        @NotBlank(message = "O email não pode ser vazio")
        @Email(message = "Formato de email inválido")
        @Schema(example = "curie@gmail.com")
        String email,

        @NotBlank(message = "A senha não pode ser vazia")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        @Schema(example = "123456")
        String password
) {}
