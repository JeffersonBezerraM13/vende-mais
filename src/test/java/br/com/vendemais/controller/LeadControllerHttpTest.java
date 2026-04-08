package br.com.vendemais.controller;

import br.com.vendemais.service.LeadService;
import br.com.vendemais.support.MockMvcSecurityConfig;
import br.com.vendemais.support.TestAuthentications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeadController.class)
@Import(MockMvcSecurityConfig.class)
class LeadControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService leadService;

    @Test
    void deleteReturnsNoContentForAdmin() throws Exception {
        mockMvc.perform(delete("/leads/4").with(TestAuthentications.admin()))
                .andExpect(status().isNoContent());

        verify(leadService).delete(4L);
    }

    @Test
    void deleteReturnsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/leads/4").with(TestAuthentications.user()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(leadService);
    }
}
