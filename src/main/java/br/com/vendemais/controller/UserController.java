package br.com.vendemais.controller;

import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.user.UserRequestDTO;
import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.service.UserService;
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
 * Publishes user management endpoints that control who can access the CRM and
 * which role set is associated with each account.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Gestao de usuarios do CRM e seus perfis de acesso.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a paged list of CRM users so administrators can manage the access
     * base without loading all accounts at once.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing user summaries
     */
    @GetMapping
    @Operation(summary = "Lista os usuarios de forma paginada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios recuperados com sucesso.")
    })
    public ResponseEntity<Page<UserResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'id' do mais novo pro mais velho
            @ParameterObject
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<UserResponseDTO> page = userService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a user account so the CRM can display profile and access
     * information.
     *
     * @param id identifier of the user to load
     * @return the requested user representation
     * @throws ObjectNotFoundException if the user does not exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario recuperado com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<UserResponseDTO> findById(@Parameter(description = "ID do usuario") @PathVariable Long id){
        UserResponseDTO userResponseDTO = userService.findById(id);
        return ResponseEntity.ok().body(userResponseDTO);
    }

    /**
     * Creates a CRM user account and hashes its credentials before persistence.
     *
     * @param dto payload describing the account to be provisioned
     * @return the created user representation together with its location header
     * @throws DataIntegrityViolationException if another account already uses the informed email
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Cria um novo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para criacao do usuario.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para criar usuarios.")
    })
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO dto){
        UserResponseDTO userResponseDTO = userService.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        userResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(userResponseDTO);
    }

    /**
     * Updates an existing user account so authentication data remains aligned
     * with administrative changes.
     *
     * @param id identifier of the user being updated
     * @param dto payload containing the revised account data
     * @return the updated user representation
     * @throws ObjectNotFoundException if the user does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload invalido para atualizacao do usuario.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            ),
            @ApiResponse(responseCode = "403", description = "Acesso negado para atualizar usuarios."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<UserResponseDTO> update(
            @Parameter(description = "ID do usuario") @PathVariable Long id,
            @RequestBody @Valid UserRequestDTO dto
    ){
        return ResponseEntity.ok(userService.update(id, dto));
    }

    /**
     * Deletes a CRM user account when access must be revoked permanently.
     *
     * @param id identifier of the user to remove
     * @return an empty response confirming the deletion
     * @throws ObjectNotFoundException if the user does not exist
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario removido com sucesso."),
            @ApiResponse(responseCode = "403", description = "Acesso negado para remover usuarios."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario nao encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID do usuario") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
