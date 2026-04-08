package br.com.vendemais.domain.dtos.stage;

import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.StageType;

public record StageResponseDTO(
        Long id,
        String name,
        String code,
        Integer position,
        StageType type,
        Long pipelineId
) {
    public static StageResponseDTO daEntidade(Stage entidade) {
        return new StageResponseDTO(
                entidade.getId(),
                entidade.getName(),
                entidade.getCode(),
                entidade.getPosition(),
                entidade.getType(),
                entidade.getPipeline() != null ? entidade.getPipeline().getId() : null
        );
    }
}