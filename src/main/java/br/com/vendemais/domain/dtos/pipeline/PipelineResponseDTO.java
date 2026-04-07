package br.com.vendemais.domain.dtos.pipeline;

import br.com.vendemais.domain.entity.Pipeline;

public record PipelineResponseDTO(
        Long id,
        String title
) {
    public static PipelineResponseDTO daEntidade(Pipeline entidade) {
        return new PipelineResponseDTO(
                entidade.getId(),
                entidade.getTitle() // Não incluí a lista de Stages aqui para simplificar, mas você pode adicionar se precisar carregar tudo junto!
        );
    }
}