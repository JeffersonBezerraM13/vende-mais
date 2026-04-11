package br.com.vendemais.domain.dtos.lead;

import br.com.vendemais.domain.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Captures the data required to create or update a lead in the CRM.
 */
@Schema(name = "LeadRequestDTO", description = "Payload para criacao ou atualizacao de leads.")
public record LeadRequestDTO(
        @NotBlank(message = "Nome não pode ser vazio")
        @Schema(example = "Bob Blue")
        String name,

        @NotBlank(message = "Telefone não pode ser vazio")
        @Schema(example = "83912345678")
        String phone,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Formato de email inválido")
        @Schema(example = "bob@gmail.com")
        String email,

        @NotNull(message = "Tipo de pessoa é obrigatório")
        @Schema(example = "COMPANY")
        PersonType personType,
        @Schema(example = "Blue Corp")
        String companyName,
        @Schema(example = "COWORKING")
        Solution interestSoluction,

        @NotNull(message = "Origem do lead não pode ser vazia")
        @Schema(example = "PHONE_CALL")
        LeadSource leadSource,

        @NotNull(message = "Forma de registro do lead não pode ser vazia")
        @Schema(example = "MANUAL")
        EntryMethod entryMethod,

        @Schema(example = "Busca 4 posicoes de coworking e sala de reuniao para atender clientes presencialmente.")
        String notes
) {}
