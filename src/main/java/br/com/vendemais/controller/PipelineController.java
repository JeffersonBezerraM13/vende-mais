package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.service.PipelineService;
import br.com.vendemais.service.StageService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


/**
 * Exposes pipeline administration endpoints used to configure sales funnels and
 * the stages that structure opportunity progression.
 */
@RestController
@RequestMapping("/pipelines")
@Tag(name = "Pipelines", description = "Configuracao dos funis comerciais e de suas etapas.")
@SecurityRequirement(name = "bearerAuth")
public class PipelineController {

    private final PipelineService pipelineService;

    private final StageService stageService;

    public PipelineController(PipelineService pipelineService, StageService stageService) {
        this.pipelineService = pipelineService;
        this.stageService = stageService;
    }

    /**
     * Returns a paged list of pipelines so CRM clients can present available
     * funnel configurations.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing pipeline summaries
     */
    @GetMapping
    @Operation(summary = "Lista os pipelines de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pipelines recuperados com sucesso.")
    })
    public ResponseEntity<Page<PipelineResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'id' do mais novo pro mais velho
            @ParameterObject
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<PipelineResponseDTO> page = pipelineService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a pipeline with its stage structure so administrators can inspect
     * the funnel definition used by opportunities.
     *
     * @param id identifier of the pipeline to load
     * @return the requested pipeline representation
     * @throws ObjectNotFoundException if the pipeline does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um pipeline por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pipeline recuperado com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<PipelineResponseDTO> findById(@Parameter(description = "ID do pipeline") @PathVariable Long id){
        PipelineResponseDTO pipelineResponseDTO = pipelineService.findById(id);
        return ResponseEntity.ok().body(pipelineResponseDTO);
    }

    /**
     * Creates a new sales pipeline so the organization can model a distinct
     * commercial process inside the CRM.
     *
     * @param pipelineRequestDTO payload describing the pipeline being created
     * @return the newly created pipeline representation
     * @throws DataIntegrityViolationException if a pipeline with the same title already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Cria um novo pipeline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pipeline criado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para criacao do pipeline.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para criar pipelines.")
    })
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody @Valid PipelineRequestDTO pipelineRequestDTO){
        PipelineResponseDTO pipelineResponseDTO = pipelineService.create(pipelineRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        pipelineResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(pipelineResponseDTO);
    }

    /**
     * Adds a stage to an existing pipeline so opportunities can move through a
     * well-defined ordered funnel.
     *
     * @param dto payload describing the stage and the pipeline it belongs to
     * @return the created stage representation
     * @throws DataIntegrityViolationException if the referenced pipeline does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/stages")
    @Operation(summary = "Cria uma etapa dentro de um pipeline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etapa criada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para criacao da etapa.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para criar etapas."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<StageResponseDTO> createStage(@RequestBody @Valid StageRequestDTO dto){
        return ResponseEntity.ok().body(stageService.createStage(dto));
    }

    /**
     * Renames an existing pipeline so funnel nomenclature stays aligned with the
     * commercial process.
     *
     * @param id identifier of the pipeline being updated
     * @param dto payload containing the new pipeline data
     * @return the updated pipeline representation
     * @throws ObjectNotFoundException if the pipeline does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um pipeline existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pipeline atualizado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para atualizacao do pipeline.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para atualizar pipelines."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<PipelineResponseDTO> update(
            @Parameter(description = "ID do pipeline") @PathVariable Long id,
            @RequestBody @Valid PipelineRequestDTO dto
    ) {
        return ResponseEntity.ok(pipelineService.update(id, dto));

    }

    /**
     * Updates the descriptive data of a stage while keeping it attached to the
     * expected pipeline.
     *
     * @param stageId identifier of the stage being updated
     * @param dto payload containing the revised stage data
     * @return the updated stage representation
     * @throws ObjectNotFoundException if the stage does not belong to the informed pipeline
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/stages/{stageId}")
    @Operation(summary = "Atualiza uma etapa de pipeline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etapa atualizada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para atualizacao da etapa.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para atualizar etapas."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Etapa nao encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<StageResponseDTO> updateStage(
            @Parameter(description = "ID da etapa") @PathVariable Long stageId,
            @RequestBody @Valid StageRequestDTO dto
    ){
        return ResponseEntity.ok().body(stageService.updateStage(stageId,dto));
    }

    /**
     * Deletes a pipeline when it is no longer part of the CRM operating model.
     *
     * @param id identifier of the pipeline to remove
     * @return an empty response confirming the deletion
     * @throws ObjectNotFoundException if the pipeline does not exist
     * @throws DataIntegrityViolationException if the pipeline is still referenced by stages or opportunities
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um pipeline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pipeline removido com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado para remover pipelines."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID do pipeline") @PathVariable Long id) {
        pipelineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
