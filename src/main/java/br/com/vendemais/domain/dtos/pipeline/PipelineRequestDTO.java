package br.com.vendemais.domain.dtos.pipeline;

import jakarta.validation.constraints.NotBlank;

public record PipelineRequestDTO(
        @NotBlank(message = "O título do pipeline não pode ser vazio")
        String title
) {}