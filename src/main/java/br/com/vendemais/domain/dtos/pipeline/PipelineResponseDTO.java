package br.com.vendemais.domain.dtos.pipeline;

import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public record PipelineResponseDTO(
        Long id,
        String title,
        List<StageResponseDTO> stages
) {
    public static PipelineResponseDTO daEntidade(Pipeline entidade) {
        return new PipelineResponseDTO(
                entidade.getId(),
                entidade.getTitle(),
                //a lista de entidades e transformamos em DTOs
                entidade.getStages() != null ?
                        entidade.getStages().stream()
                                .map(StageResponseDTO::daEntidade)
                                .collect(Collectors.toList())
                        : List.of()
        );
    }
}