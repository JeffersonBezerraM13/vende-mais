package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.service.TaskService;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


/**
 * Exposes task endpoints used to schedule and track follow-up activities linked
 * to leads and opportunities.
 */
@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Gestao de tarefas vinculadas a leads e oportunidades.")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    /**
     * Returns a paged list of tasks so users can review pending and completed CRM
     * follow-ups.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing task summaries
     */
    @GetMapping
    @Operation(summary = "Lista as tarefas de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas recuperadas com sucesso.")
    })
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'createdAt' do mais novo pro mais velho
            @ParameterObject
            @PageableDefault(page = 0,size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<TaskResponseDTO> page = taskService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a task so the CRM can display its schedule, status, and related
     * commercial record.
     *
     * @param id identifier of the task to load
     * @return the requested task representation
     * @throws ObjectNotFoundException if the task does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca uma tarefa por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa recuperada com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa nao encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> findById(@Parameter(description = "ID da tarefa") @PathVariable Long id){
        TaskResponseDTO taskResponseDTO = taskService.findById(id);
        return ResponseEntity.ok().body(taskResponseDTO);
    }

    /**
     * Creates a follow-up task tied to a lead or opportunity so execution can be
     * tracked inside the CRM.
     *
     * @param dto payload describing the activity to be scheduled
     * @return the created task representation together with its location header
     * @throws DataIntegrityViolationException if the task is not linked to a valid lead or opportunity
     */
    @PostMapping
    @Operation(summary = "Cria uma nova tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para criacao da tarefa.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> create(@RequestBody @Valid TaskRequestDTO dto){
        TaskResponseDTO taskResponseDTO = taskService.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        taskResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(taskResponseDTO);
    }

    /**
     * Updates a task so deadlines, status, and ownership context stay aligned
     * with the latest follow-up plan.
     *
     * @param id identifier of the task being updated
     * @param dto payload containing the new task state
     * @return the updated task representation
     * @throws ObjectNotFoundException if the task or one of its referenced records does not exist
     * @throws DataIntegrityViolationException if the task is left without a lead or opportunity linkage
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para atualizacao da tarefa.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa nao encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<TaskResponseDTO> update(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @RequestBody @Valid TaskRequestDTO dto
    ){
        return ResponseEntity.ok(taskService.update(id, dto));
    }

    /**
     * Deletes a task once the follow-up is no longer relevant to the commercial
     * process.
     *
     * @param id identifier of the task to remove
     * @return an empty response confirming the deletion
     * @throws ObjectNotFoundException if the task does not exist
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tarefa nao encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID da tarefa") @PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
