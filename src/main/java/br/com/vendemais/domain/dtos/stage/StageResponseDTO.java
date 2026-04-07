package br.com.vendemais.domain.dtos.stage;

import br.com.vendemais.domain.entity.Stage;

public record StageResponseDTO(
        Long id,
        Integer number,
        String title
) {
    public static StageResponseDTO daEntidade(Stage entidade) {
        return new StageResponseDTO(
                entidade.getId(),
                entidade.getNumber(),
                entidade.getTitle()
        );
    }
}