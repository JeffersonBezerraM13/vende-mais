package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.security.UsuarioSecurity;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal UsuarioSecurity usuarioLogado) {
        User userCompleto = userRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado."));

        return ResponseEntity.ok(UserResponseDTO.daEntidade(userCompleto));
    }
}