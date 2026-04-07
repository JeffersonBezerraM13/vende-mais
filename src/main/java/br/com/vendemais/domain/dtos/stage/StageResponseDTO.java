package br.com.vendemais.domain.dtos.stage;

import br.com.vendemais.domain.entity.Stage;

public record StageResponseDTO(
        Long id,
        String name,
        String code,
        Integer position,
        Boolean finalStage,
        Long pipelineId
) {
    public static StageResponseDTO daEntidade(Stage entidade) {
        return new StageResponseDTO(
                entidade.getId(),
                entidade.getName(),
                entidade.getCode(),
                entidade.getPosition(),
                entidade.getFinalStage(),
                entidade.getPipeline() != null ? entidade.getPipeline().getId() : null
        );
    }
}