package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.user.UserRequestDTO;
import br.com.vendemais.domain.dtos.user.UserResponseDTO;
import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.service.UserService;
import br.com.vendemais.support.MockMvcSecurityConfig;
import br.com.vendemais.support.TestAuthentications;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(MockMvcSecurityConfig.class)
class UserControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createReturnsCreatedForAdmin() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Marie Curie", "marie@gmail.com", "123456");
        UserResponseDTO response = new UserResponseDTO(10L, "Marie Curie", "marie@gmail.com", Set.of(Role.USER));

        when(userService.create(request)).thenReturn(response);

        mockMvc.perform(post("/users")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Marie Curie"));
    }

    @Test
    void createReturnsForbiddenForNonAdmin() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Marie Curie", "marie@gmail.com", "123456");

        mockMvc.perform(post("/users")
                        .with(TestAuthentications.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void updateReturnsOkForAdmin() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Ada Lovelace", "ada@gmail.com", "123456");
        UserResponseDTO response = new UserResponseDTO(7L, "Ada Lovelace", "ada@gmail.com", Set.of(Role.ADMIN, Role.USER));

        when(userService.update(eq(7L), eq(request))).thenReturn(response);

        mockMvc.perform(put("/users/7")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("ada@gmail.com"));
    }

    @Test
    void deleteReturnsNoContentForAdmin() throws Exception {
        mockMvc.perform(delete("/users/7").with(TestAuthentications.admin()))
                .andExpect(status().isNoContent());

        verify(userService).delete(7L);
    }

    @Test
    void deleteReturnsUnauthorizedWhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/users/7"))
                .andExpect(status().isUnauthorized());
    }
}
