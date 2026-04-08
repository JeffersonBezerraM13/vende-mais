package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.security.UsuarioSecurity;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "userRepository", userRepository);
    }

    @Test
    void getMeReturnsAuthenticatedUserData() {
        User user = new User("Albert Einstein", "einstein@gmail.com", "encoded");
        user.addRole(Role.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        UsuarioSecurity principal = new UsuarioSecurity(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<UserResponseDTO> response = authController.getMe(principal);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().id());
        assertEquals("Albert Einstein", response.getBody().name());
        assertEquals("einstein@gmail.com", response.getBody().email());
        assertIterableEquals(user.getRoles(), response.getBody().roles());
    }

    @Test
    void getMeThrowsWhenUserIsMissing() {
        UsuarioSecurity principal = new UsuarioSecurity(99L, "missing@gmail.com", "encoded", java.util.Set.of(Role.USER));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> authController.getMe(principal));
    }
}
