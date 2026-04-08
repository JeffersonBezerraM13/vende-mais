package br.com.vendemais.controller;

import br.com.vendemais.service.OpportunityService;
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

@WebMvcTest(OpportunityController.class)
@Import(MockMvcSecurityConfig.class)
class OpportunityControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpportunityService opportunityService;

    @Test
    void deleteReturnsNoContentForAdmin() throws Exception {
        mockMvc.perform(delete("/opportunities/8").with(TestAuthentications.admin()))
                .andExpect(status().isNoContent());

        verify(opportunityService).delete(8L);
    }

    @Test
    void deleteReturnsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/opportunities/8").with(TestAuthentications.user()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(opportunityService);
    }
}
