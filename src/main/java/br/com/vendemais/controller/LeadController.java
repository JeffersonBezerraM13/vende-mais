package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.lead.LeadFilterDTO;
import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.service.LeadService;
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
 * Exposes lead endpoints used to register, browse, and maintain prospects across
 * the CRM funnel.
 */
@RestController
@RequestMapping("/leads")
@Tag(name = "Leads", description = "Gerenciamento de leads e clientes em potencial no funil comercial.")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    /**
     * Returns a paged view of leads so clients can populate prospect lists and
     * qualification backlogs.
     *
     * @param pageable pagination and sorting instructions provided by the client
     * @return a page containing lead summaries ordered according to the request
     */
    @GetMapping
    @Operation(summary = "Lista os leads de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leads recuperados com sucesso.")
    })
    public ResponseEntity<Page<LeadResponseDTO>> findAll(
            @Valid @ParameterObject LeadFilterDTO filter,

            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<LeadResponseDTO> page = leadService.findAll(filter, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a single lead so the CRM can render prospect details, notes, and
     * next actions.
     *
     * @param id unique identifier of the lead to be loaded
     * @return the requested lead representation
     * @throws ObjectNotFoundException if the lead does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um lead por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead recuperado com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<LeadResponseDTO> findById(@Parameter(description = "ID do lead") @PathVariable Long id){
        LeadResponseDTO dto = leadService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Creates a new lead captured from inbound or outbound channels so the sales
     * team can start qualification.
     *
     * @param dto payload describing the prospect being registered
     * @return the newly created lead representation together with its location header
     * @throws DataIntegrityViolationException if another lead already uses the informed email
     */
    @PostMapping
    @Operation(summary = "Cria um novo lead")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lead criado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para criacao do lead.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            )
    })
    public ResponseEntity<LeadResponseDTO> create(@RequestBody @Valid LeadRequestDTO dto){
        LeadResponseDTO leadResponseDTO = leadService.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        leadResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(leadResponseDTO);
    }

    /**
     * Updates an existing lead so prospect data remains consistent across CRM
     * qualification and follow-up workflows.
     *
     * @param id identifier of the lead being revised
     * @param dto payload containing the latest lead data
     * @return the updated lead representation
     * @throws ObjectNotFoundException if the lead does not exist
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um lead existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead atualizado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para atualizacao do lead.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<LeadResponseDTO> update(
            @Parameter(description = "ID do lead") @PathVariable Long id,
            @RequestBody @Valid LeadRequestDTO dto
    ){
        return ResponseEntity.ok(leadService.update(id, dto));
    }

    /**
     * Deletes a lead that should no longer remain in the CRM prospect base.
     *
     * @param id identifier of the lead to be removed
     * @return an empty response confirming the resource removal
     * @throws ObjectNotFoundException if the lead does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um lead")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lead removido com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado para remover leads."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lead nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID do lead") @PathVariable Long id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
