package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.service.StageService;
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
import org.springframework.beans.factory.annotation.Autowired;
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
 * Provides read-only endpoints for consulting the stages configured inside CRM
 * pipelines.
 */
@RestController
@RequestMapping("/stages")
@Tag(name = "Stages", description = "Consulta das etapas cadastradas nos pipelines.")
@SecurityRequirement(name = "bearerAuth")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    /**
     * Returns a paged list of stages so clients can browse funnel checkpoints
     * configured in the CRM.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing stage summaries
     */
    @GetMapping
    @Operation(summary = "Lista as etapas de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etapas recuperadas com sucesso.")
    })
    public ResponseEntity<Page<StageResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'id' do mais novo pro mais velho
            @ParameterObject
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<StageResponseDTO> page = stageService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a single stage so clients can inspect its position and owning
     * pipeline.
     *
     * @param id identifier of the stage to load
     * @return the requested stage representation
     * @throws ObjectNotFoundException if the stage does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca uma etapa por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etapa recuperada com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Etapa nao encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<StageResponseDTO> findById(@Parameter(description = "ID da etapa") @PathVariable Long id) {
        StageResponseDTO stageResponseDTO = stageService.findById(id);
        return ResponseEntity.ok().body(stageResponseDTO);
    }
}
