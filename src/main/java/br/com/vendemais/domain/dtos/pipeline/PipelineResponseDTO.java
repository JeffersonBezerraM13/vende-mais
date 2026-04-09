package br.com.vendemais.domain.dtos.pipeline;

import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a pipeline together with its ordered stage structure as exposed by
 * the API.
 */
@Schema(name = "PipelineResponseDTO", description = "Representacao de pipeline retornada pela API.")
public record PipelineResponseDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Funil Comercial")
        String title,
        @Schema(example = "[{\"id\":1,\"name\":\"Novo lead\",\"code\":\"NOVO_LEAD\",\"position\":1,\"pipelineId\":1},{\"id\":2,\"name\":\"Contato inicial\",\"code\":\"CONTATO_INICIAL\",\"position\":2,\"pipelineId\":1}]")
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
