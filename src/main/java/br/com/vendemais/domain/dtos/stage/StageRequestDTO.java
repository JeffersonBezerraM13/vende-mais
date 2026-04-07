package br.com.vendemais.domain.dtos.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StageRequestDTO(
        @NotNull(message = "O número do estágio é obrigatório")
        Integer number,

        @NotBlank(message = "O título do estágio não pode ser vazio")
        String title
) {}