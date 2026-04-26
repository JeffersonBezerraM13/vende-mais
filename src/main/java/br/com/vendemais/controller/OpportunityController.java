package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.opportunity.OpportunityCloseDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityFilterDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.service.OpportunityService;
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
 * Publishes opportunity endpoints used to progress qualified leads through sales
 * pipelines until they are won or lost.
 */
@RestController
@RequestMapping("/opportunities")
@Tag(name = "Opportunities", description = "Gestão de oportunidades vinculadas ao funil de vendas.")
@SecurityRequirement(name = "bearerAuth")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    /**
     * Returns a paged and filtered list of opportunities so kanban boards and
     * portfolio views can be rendered without client-side array filtering.
     *
     * @param filter optional search, status, and pipeline filtering criteria
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered opportunity summaries
     */
    @GetMapping
    @Operation(summary = "Lista as oportunidades de forma paginada e filtrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oportunidades recuperadas com sucesso.")
    })
    public ResponseEntity<Page<OpportunityResponseDTO>> findAll(
            @Valid @ParameterObject OpportunityFilterDTO filter,

            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<OpportunityResponseDTO> page = opportunityService.findAll(filter, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves the full details of a single opportunity so clients can display
     * its commercial context and current pipeline stage.
     *
     * @param id identifier of the opportunity to load
     * @return the requested opportunity representation
     * @throws ObjectNotFoundException if the opportunity does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca uma oportunidade por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oportunidade recuperada com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Oportunidade não encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<OpportunityResponseDTO> findById(@Parameter(description = "ID da oportunidade") @PathVariable Long id){
        OpportunityResponseDTO opportunityResponseDTO = opportunityService.findById(id);
        return ResponseEntity.ok().body(opportunityResponseDTO);
    }

    /**
     * Checks whether a lead already has active commercial negotiations before a
     * new opportunity is opened.
     *
     * @param leadId identifier of the lead being inspected
     * @return {@code true} when at least one open opportunity exists for the lead
     * @throws IllegalArgumentException if {@code leadId} is {@code null}
     */
    @GetMapping("/check-open")
    @Operation(summary = "Verifica se o lead possui oportunidades abertas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação executada com sucesso.")
    })
    public ResponseEntity<Boolean> hasOpenOpportunities(@Parameter(description = "ID do lead") @RequestParam Long leadId) {
        boolean hasOpen = opportunityService.hasOpenOpportunities(leadId);
        return ResponseEntity.ok(hasOpen);
    }

    /**
     * Creates a new opportunity for a qualified lead inside the selected sales
     * pipeline.
     *
     * @param dto payload describing the commercial opportunity to be tracked
     * @return the created opportunity representation together with its location header
     * @throws DataIntegrityViolationException if referenced lead, pipeline, or stage data is invalid
     */
    @PostMapping
    @Operation(summary = "Cria uma nova oportunidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Oportunidade criada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido para criação da oportunidade.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            )
    })
    public ResponseEntity<OpportunityResponseDTO> create(@RequestBody @Valid OpportunityRequestDTO dto){
        OpportunityResponseDTO opportunityResponseDTO = opportunityService.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        opportunityResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(opportunityResponseDTO);
    }

    /**
     * Revises an opportunity so its owner, pipeline context, or forecast can be
     * kept aligned with the latest negotiation state.
     *
     * @param id identifier of the opportunity being updated
     * @param dto payload containing the new opportunity state
     * @return the updated opportunity representation
     * @throws ObjectNotFoundException if the opportunity does not exist
     * @throws DataIntegrityViolationException if referenced lead, pipeline, or stage data is invalid
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma oportunidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oportunidade atualizada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido para atualização da oportunidade.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Oportunidade não encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<OpportunityResponseDTO> update(
            @Parameter(description = "ID da oportunidade") @PathVariable Long id,
            @RequestBody @Valid OpportunityRequestDTO dto
    ){
        return ResponseEntity.ok(opportunityService.update(id, dto));
    }

    /**
     * Closes an opportunity as won or lost, enforcing the CRM rule that lost
     * deals must provide a reason.
     *
     * @param id identifier of the opportunity being closed
     * @param dto payload indicating whether the deal was won and, if lost, why
     * @return the closed opportunity representation
     * @throws ObjectNotFoundException if the opportunity does not exist
     * @throws DataIntegrityViolationException if the opportunity is already closed or the loss reason is missing
     */
    @PatchMapping("/{id}/close")
    @Operation(summary = "Fecha uma oportunidade como ganha ou perdida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Oportunidade fechada com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido para fechamento da oportunidade.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Oportunidade não encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<OpportunityResponseDTO> close(
            @Parameter(description = "ID da oportunidade") @PathVariable Long id,
            @RequestBody @Valid OpportunityCloseDTO dto
    ) {
        return ResponseEntity.ok(opportunityService.close(id, dto));
    }

    /**
     * Deletes an opportunity together with its dependent tasks when the
     * negotiation should no longer remain in the CRM.
     *
     * @param id identifier of the opportunity to remove
     * @return an empty response confirming the deletion
     * @throws ObjectNotFoundException if the opportunity does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma oportunidade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Oportunidade removida com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado para remover oportunidades."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Oportunidade não encontrada.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID da oportunidade") @PathVariable Long id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
