package br.com.vendemais.controller;

import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.repository.UserRepository;
import br.com.vendemais.support.MockMvcSecurityConfig;
import br.com.vendemais.support.TestAuthentications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(MockMvcSecurityConfig.class)
class AuthControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getMeReturnsUnauthorizedWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMeReturnsAuthenticatedUserPayload() throws Exception {
        User user = new User("Albert Einstein", "einstein@gmail.com", "encoded");
        user.addRole(Role.ADMIN);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/auth/me").with(TestAuthentications.principal(1L, "einstein@gmail.com", Role.ADMIN, Role.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Albert Einstein"))
                .andExpect(jsonPath("$.email").value("einstein@gmail.com"))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("ADMIN", "USER")));
    }

    @Test
    void getMeReturnsNotFoundWhenAuthenticatedUserDoesNotExist() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/me").with(TestAuthentications.principal(99L, "missing@gmail.com", Role.USER)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/auth/me"));
    }
}
