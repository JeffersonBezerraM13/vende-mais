package br.com.vendemais.domain.dtos.task;

import br.com.vendemais.domain.enums.filter.TaskDeadlineFilter;
import br.com.vendemais.domain.enums.filter.TaskLinkTypeFilter;
import br.com.vendemais.domain.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Represents optional filtering criteria used to search and narrow task listings.
 */
@Schema(name = "TaskFilterDTO", description = "Parâmetros opcionais para filtragem da listagem de tarefas.")
public record TaskFilterDTO(
        @Size(max = 120, message = "O termo de busca deve ter no máximo 120 caracteres")
        @Schema(
                description = "Termo usado para buscar tarefas por título ou descrição.",
                example = "contrato"
        )
        String search,

        @Schema(
                description = "Filtra tarefas pelo status.",
                example = "PENDING"
        )
        TaskStatus status,

        @Schema(
                description = "Filtra tarefas pelo prazo: atrasadas ou vencendo nos próximos 3 dias.",
                example = "OVERDUE"
        )
        TaskDeadlineFilter deadline,

        @Schema(
                description = "Filtra tarefas pelo tipo de vínculo.",
                example = "OPPORTUNITY"
        )
        TaskLinkTypeFilter linkType
) {
}
