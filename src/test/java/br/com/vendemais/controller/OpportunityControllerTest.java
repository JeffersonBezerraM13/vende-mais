package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.service.OpportunityService;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OpportunityControllerTest extends ControllerTestSupport {

    @Mock
    private OpportunityService opportunityService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(new OpportunityController(opportunityService));
    }

    @Test
    void createShouldReturn201WhenPayloadIsValid() throws Exception {
        OpportunityRequestDTO request = new OpportunityRequestDTO(
                1L,
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("1500.00"),
                2L,
                3L,
                LocalDate.of(2026, 5, 10),
                "Observacao"
        );
        OpportunityResponseDTO response = new OpportunityResponseDTO(
                10L,
                1L,
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("1500.00"),
                2L,
                3L,
                "Contato",
                false,
                LocalDate.of(2026, 5, 10),
                null,
                null,
                "Observacao",
                LocalDate.of(2026, 4, 8),
                null
        );

        when(opportunityService.create(request)).thenReturn(response);

        mockMvc.perform(post("/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/opportunities/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.leadId").value(1))
                .andExpect(jsonPath("$.currentStageId").value(3));
    }

    @Test
    void createShouldReturn400WhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erros[*].fieldName",
                        hasItems("leadId", "title", "definitiveSolution", "pipelineId")));
    }

    @Test
    void createShouldReturn400WhenServiceRejectsRequest() throws Exception {
        OpportunityRequestDTO request = new OpportunityRequestDTO(
                1L,
                "Nova oportunidade",
                Solution.COWORKING,
                new BigDecimal("1500.00"),
                2L,
                9L,
                LocalDate.of(2026, 5, 10),
                null
        );

        when(opportunityService.create(any(OpportunityRequestDTO.class)))
                .thenThrow(new DataIntegrityViolationException("A etapa informada nao pertence ao pipeline selecionado."));

        mockMvc.perform(post("/opportunities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("A etapa informada nao pertence ao pipeline selecionado."));
    }

    @Test
    void hasOpenOpportunitiesShouldReturn200AndBooleanBody() throws Exception {
        when(opportunityService.hasOpenOpportunities(1L)).thenReturn(true);

        mockMvc.perform(get("/opportunities/check-open").param("leadId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
