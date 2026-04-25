package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.domain.enums.TaskStatus;
import br.com.vendemais.service.TaskService;
import br.com.vendemais.service.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static br.com.vendemais.support.TestAuthentications.admin;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest extends ControllerTestSupport {

    @Mock
    private TaskService taskService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(new TaskController(taskService));
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso vinculada a uma oportunidade")
    void createShouldReturn201WhenPayloadIsLinkedToOpportunity() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Enviar proposta",
                "Detalhes",
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                null,
                10L
        );

        TaskResponseDTO response = new TaskResponseDTO(
                21L,
                1L,
                "Enviar proposta",
                "Detalhes",
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                null,
                10L,
                LocalDate.of(2026, 4, 8),
                null
        );

        when(taskService.create(any(TaskRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/tasks/21")))
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.opportunityId").value(10))
                .andExpect(jsonPath("$.leadId").isEmpty());
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso vinculada a um lead")
    void createShouldReturn201WhenPayloadIsLinkedToLead() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Ligar para lead",
                "Qualificação inicial",
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                5L,
                null
        );

        TaskResponseDTO response = new TaskResponseDTO(
                22L,
                1L,
                "Ligar para lead",
                "Qualificação inicial",
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                5L,
                null,
                LocalDate.of(2026, 4, 8),
                null
        );

        when(taskService.create(any(TaskRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/tasks/22")))
                .andExpect(jsonPath("$.id").value(22))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.leadId").value(5))
                .andExpect(jsonPath("$.opportunityId").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar 400 quando campos obrigatórios estão nulos")
    void createShouldReturn400WhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/tasks")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erros[*].fieldName", hasItems("title", "dueDate")));
    }

    @Test
    @DisplayName("Deve retornar 422 quando tarefa não possui vínculo comercial")
    void createShouldReturn422WhenTaskHasNoCommercialLink() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Tarefa sem vínculo",
                null,
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                null,
                null
        );

        when(taskService.create(any(TaskRequestDTO.class)))
                .thenThrow(new BusinessRuleException("Toda tarefa deve estar vinculada a um Lead ou a uma Oportunidade."));

        mockMvc.perform(post("/tasks")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message")
                        .value("Toda tarefa deve estar vinculada a um Lead ou a uma Oportunidade."));
    }

    @Test
    @DisplayName("Deve retornar 422 quando tarefa possui lead e oportunidade ao mesmo tempo")
    void createShouldReturn422WhenTaskHasLeadAndOpportunityAtSameTime() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO(
                "Tarefa com vínculos duplicados",
                null,
                TaskStatus.PENDING,
                LocalDate.of(2026, 4, 20),
                5L,
                10L
        );

        when(taskService.create(any(TaskRequestDTO.class)))
                .thenThrow(new BusinessRuleException("A tarefa não pode estar vinculada a um Lead e a uma Oportunidade ao mesmo tempo."));

        mockMvc.perform(post("/tasks")
                        .with(admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message")
                        .value("A tarefa não pode estar vinculada a um Lead e a uma Oportunidade ao mesmo tempo."));
    }

    @Test
    @DisplayName("Deve retornar 204 ao excluir tarefa")
    void deleteShouldReturn204() throws Exception {
        doNothing().when(taskService).delete(7L);

        mockMvc.perform(delete("/tasks/7")
                        .with(admin()))
                .andExpect(status().isNoContent());

        verify(taskService).delete(7L);
    }
}