package br.com.vendemais.domain.dtos.lead;

import br.com.vendemais.domain.enums.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeadRequestDTO(
        @NotBlank(message = "Nome não pode ser vazio")
        String name,

        @NotBlank(message = "Telefone não pode ser vazio")
        String phone,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Formato de email inválido")
        String email,

        PersonType personType,
        String companyName,
        Solution interestSoluction,

        @NotNull(message = "Origem do lead não pode ser vazia")
        LeadSource leadSource,

        @NotNull(message = "Forma de registro do lead não pode ser vazia")
        EntryMethod entryMethod,

        String notes
) {}
