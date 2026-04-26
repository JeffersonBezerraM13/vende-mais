package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.service.LeadService;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LeadControllerTest extends ControllerTestSupport {

    @Mock
    private LeadService leadService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(new LeadController(leadService));
    }

    @Test
    void createShouldReturn201WhenPayloadIsValid() throws Exception {
        LeadRequestDTO request = new LeadRequestDTO(
                "Lead Teste",
                "11999999999",
                "lead@example.com",
                PersonType.COMPANY,
                "Empresa Teste",
                Solution.COWORKING,
                LeadSource.SITE,
                EntryMethod.MANUAL,
                "Observacao"
        );
        LeadResponseDTO response = new LeadResponseDTO(
                11L,
                "Lead Teste",
                "11999999999",
                "lead@example.com",
                PersonType.COMPANY,
                "Empresa Teste",
                Solution.COWORKING,
                LeadSource.SITE,
                EntryMethod.MANUAL,
                "Observacao",
                LocalDate.of(2026, 4, 8),
                null
        );

        when(leadService.create(request)).thenReturn(response);

        mockMvc.perform(post("/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/leads/11")))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.email").value("lead@example.com"));
    }

    @Test
    void createShouldReturn400WhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erros[*].fieldName",
                        hasItems("name", "phone", "email", "personType", "leadSource", "entryMethod")));
    }

    @Test
    void findByIdShouldReturn404WhenLeadDoesNotExist() throws Exception {
        when(leadService.findById(99L)).thenThrow(new ObjectNotFoundException("Lead não encontrado"));

        mockMvc.perform(get("/leads/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Lead não encontrado"));
    }
}
