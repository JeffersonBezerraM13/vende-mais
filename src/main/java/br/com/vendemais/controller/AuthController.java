package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.security.UserSecurity;
import br.com.vendemais.controller.exceptions.StandardError;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides authenticated-user endpoints that let clients resolve the CRM profile
 * associated with the current bearer token.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Operações relacionadas ao login e ao contexto do usuário autenticado.")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the full CRM user profile for the authenticated principal so clients
     * can bootstrap profile data and authorization-dependent screens.
     *
     * @param loggedUser security principal extracted from the validated JWT
     * @return the complete user representation bound to the current session
     * @throws ObjectNotFoundException if the authenticated principal no longer exists in persistence
     */
    @GetMapping("/me")
    @Operation(summary = "Retorna o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado recuperado com sucesso."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário autenticado não encontrado.",
                    content = @Content(schema = @Schema(implementation = StandardError.class))
            )
    })
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal UserSecurity loggedUser) {
        User user = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado."));

        return ResponseEntity.ok(UserResponseDTO.daEntidade(user));
    }
}
