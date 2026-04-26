package br.com.vendemais.controller.integration;

import br.com.vendemais.controller.exceptions.ValidationError;
import br.com.vendemais.domain.dtos.integration.MarketingLeadImportRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.service.integration.MarketingLeadIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Publishes integration endpoints that simulate external marketing platforms
 * sending qualified contacts into the CRM lead base.
 */
@RestController
@RequestMapping("/integrations/marketing/leads")
@Tag(name = "Marketing Integrations", description = "Integrações externas para importação de leads de marketing.")
@SecurityRequirement(name = "bearerAuth")
public class MarketingLeadIntegrationController {

    private final MarketingLeadIntegrationService marketingLeadIntegrationService;

    public MarketingLeadIntegrationController(MarketingLeadIntegrationService marketingLeadIntegrationService) {
        this.marketingLeadIntegrationService = marketingLeadIntegrationService;
    }

    /**
     * Imports leads from an external public API to simulate contacts captured by
     * marketing campaigns and stores them as CRM leads.
     *
     * @param dto payload defining how many external leads should be imported
     * @return imported leads persisted in the CRM
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/import")
    @Operation(summary = "Importa leads de uma API externa de marketing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leads importados com sucesso."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido para importação de leads.",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))
            )
    })
    public ResponseEntity<List<LeadResponseDTO>> importLeads(
            @RequestBody @Valid MarketingLeadImportRequestDTO dto
    ) {
        List<LeadResponseDTO> leads = marketingLeadIntegrationService.importMarketingLeads(dto);
        return ResponseEntity.ok(leads);
    }
}
