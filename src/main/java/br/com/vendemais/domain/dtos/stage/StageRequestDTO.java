package br.com.vendemais.domain.dtos.stage;

import br.com.vendemais.domain.enums.StageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StageRequestDTO(
        @NotBlank(message = "O nome do estágio não pode ser vazio")
        String name,

        @NotBlank(message = "O código do estágio não pode ser vazio")
        String code,

        @NotNull(message = "A posição do estágio é obrigatória")
        Integer position,

        @NotNull(message = "O id do funíl precisa ser informado")
        Long pipelineId
) {}