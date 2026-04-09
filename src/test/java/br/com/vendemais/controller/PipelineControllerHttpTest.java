package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.service.PipelineService;
import br.com.vendemais.service.StageService;
import br.com.vendemais.support.MockMvcSecurityConfig;
import br.com.vendemais.support.TestAuthentications;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@WebMvcTest(PipelineController.class)
@Import(MockMvcSecurityConfig.class)
class PipelineControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PipelineService pipelineService;

    @MockitoBean
    private StageService stageService;

    @Test
    void createReturnsCreatedForAdmin() throws Exception {
        PipelineRequestDTO request = new PipelineRequestDTO("Sales Funnel");
        PipelineResponseDTO response = new PipelineResponseDTO(3L, "Sales Funnel", List.of());

        when(pipelineService.create(request)).thenReturn(response);

        mockMvc.perform(post("/pipelines")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/pipelines/3"))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Sales Funnel"));
    }

    @Test
    void createReturnsForbiddenForNonAdmin() throws Exception {
        PipelineRequestDTO request = new PipelineRequestDTO("Sales Funnel");

        mockMvc.perform(post("/pipelines")
                        .with(TestAuthentications.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pipelineService);
    }

    @Test
    void createStageReturnsOkForAdmin() throws Exception {
        StageRequestDTO request = new StageRequestDTO("Qualified", "QUALIFIED", 2, 3L);
        StageResponseDTO response = new StageResponseDTO(9L, "Qualified", "QUALIFIED", 2, 3L);

        when(stageService.createStage(eq(request))).thenReturn(response);

        mockMvc.perform(post("/pipelines/stages")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.pipelineId").value(3));
    }

    @Test
    void createStageReturnsForbiddenForNonAdmin() throws Exception {
        StageRequestDTO request = new StageRequestDTO("Qualified", "QUALIFIED", 2, 3L);

        mockMvc.perform(post("/pipelines/stages")
                        .with(TestAuthentications.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(stageService);
    }

    @Test
    void updateReturnsOkForAdmin() throws Exception {
        PipelineRequestDTO request = new PipelineRequestDTO("Updated Funnel");
        PipelineResponseDTO response = new PipelineResponseDTO(3L, "Updated Funnel", List.of());

        when(pipelineService.update(eq(3L), eq(request))).thenReturn(response);

        mockMvc.perform(put("/pipelines/3")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Funnel"));
    }

    @Test
    void updateReturnsForbiddenForNonAdmin() throws Exception {
        PipelineRequestDTO request = new PipelineRequestDTO("Updated Funnel");

        mockMvc.perform(put("/pipelines/3")
                        .with(TestAuthentications.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pipelineService);
    }

    @Test
    void updateStageReturnsOkForAdmin() throws Exception {
        StageRequestDTO request = new StageRequestDTO("Proposal", "PROPOSAL", 4, 3L);
        StageResponseDTO response = new StageResponseDTO(11L, "Proposal", "PROPOSAL", 4, 3L);

        when(stageService.updateStage(eq(11L), eq(request))).thenReturn(response);

        mockMvc.perform(put("/pipelines/stages/11")
                        .with(TestAuthentications.admin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.code").value("PROPOSAL"));
    }

    @Test
    void updateStageReturnsForbiddenForNonAdmin() throws Exception {
        StageRequestDTO request = new StageRequestDTO("Proposal", "PROPOSAL", 4, 3L);

        mockMvc.perform(put("/pipelines/stages/11")
                        .with(TestAuthentications.user())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(stageService);
    }

    @Test
    void deleteReturnsNoContentForAdmin() throws Exception {
        mockMvc.perform(delete("/pipelines/3").with(TestAuthentications.admin()))
                .andExpect(status().isNoContent());

        verify(pipelineService).delete(3L);
    }

    @Test
    void deleteReturnsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/pipelines/3").with(TestAuthentications.user()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pipelineService);
    }
}
