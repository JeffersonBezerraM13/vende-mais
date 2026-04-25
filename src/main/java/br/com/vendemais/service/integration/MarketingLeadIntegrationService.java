package br.com.vendemais.service.integration;

import br.com.vendemais.domain.dtos.integration.MarketingLeadImportRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.infrastructure.random.user.api.RandomUserApiClient;
import br.com.vendemais.infrastructure.random.user.api.dto.RandomUserApiResponseDTO;
import br.com.vendemais.infrastructure.random.user.api.dto.RandomUserLocationDTO;
import br.com.vendemais.infrastructure.random.user.api.dto.RandomUserResultDTO;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Imports simulated marketing leads from an external API and maps them into the
 * CRM lead domain model.
 */
@Service
@Transactional(readOnly = true)
public class MarketingLeadIntegrationService {

    private final RandomUserApiClient randomUserApiClient;
    private final LeadRepository leadRepository;

    public MarketingLeadIntegrationService(RandomUserApiClient randomUserApiClient, LeadRepository leadRepository) {
        this.randomUserApiClient = randomUserApiClient;
        this.leadRepository = leadRepository;
    }

    /**
     * Imports leads from the external Random User API, converts each external
     * profile into a CRM lead, skips duplicated emails, and returns only the
     * records persisted during the operation.
     *
     * @param dto payload containing the number of external leads to import
     * @return imported leads mapped to response DTOs
     * @throws DataIntegrityViolationException if the external API returns no usable data
     */
    @Transactional
    public List<LeadResponseDTO> importMarketingLeads(MarketingLeadImportRequestDTO dto) {
        RandomUserApiResponseDTO response = randomUserApiClient.getRandomUsers(dto.quantity())
                .orElseThrow(() -> new DataIntegrityViolationException("Não foi possível importar leads da API externa."));

        if (response.results() == null || response.results().isEmpty()) {
            throw new DataIntegrityViolationException("A API externa não retornou leads para importação.");
        }

        List<Lead> leads = response.results()
                .stream()
                .filter(this::hasRequiredData)
                .filter(user -> !leadRepository.existsByEmail(user.email()))
                .map(this::mapToLead)
                .toList();

        if (leads.isEmpty()) {
            throw new DataIntegrityViolationException("Nenhum lead novo foi importado. Os registros retornados já existem ou estão incompletos.");
        }

        return leadRepository.saveAll(leads)
                .stream()
                .map(LeadResponseDTO::daEntidade)
                .toList();
    }

    private boolean hasRequiredData(RandomUserResultDTO user) {
        return user != null
                && user.name() != null
                && user.name().first() != null
                && user.name().last() != null
                && user.email() != null
                && !user.email().isBlank();
    }

    private Lead mapToLead(RandomUserResultDTO user) {
        String fullName = user.name().first() + " " + user.name().last();
        String phone = resolvePhone(user);
        String notes = buildNotes(user);

        return new Lead(
                fullName,
                phone,
                user.email(),
                PersonType.INDIVIDUAL,
                null,
                Solution.NOT_SPECIFIED,
                LeadSource.MARKETING_INTEGRATION,
                EntryMethod.INTEGRATION,
                notes
        );
    }

    private String resolvePhone(RandomUserResultDTO user) {
        if (user.cell() != null && !user.cell().isBlank()) {
            return user.cell();
        }

        if (user.phone() != null && !user.phone().isBlank()) {
            return user.phone();
        }

        return "not-informed";
    }

    /**
     * Builds a business-friendly note describing the external origin of the imported
     * marketing lead without exposing unnecessary technical payload details.
     *
     * @param user external user profile returned by the Random User API
     * @return formatted notes to be stored in the CRM lead
     */
    private String buildNotes(RandomUserResultDTO user) {
        RandomUserLocationDTO location = user.location();

        String city = location != null ? location.city() : "não informado";
        String state = location != null ? location.state() : "não informado";
        String country = location != null ? location.country() : "não informado";
        String postcode = location != null && location.postcode() != null
                ? String.valueOf(location.postcode())
                : "não informado";

        String registeredDate = user.registered() != null && user.registered().date() != null
                ? user.registered().date()
                : "não informado";

        return """
            Lead importado automaticamente por integração externa de marketing.

            Origem da integração: Random User API
            Contexto: simulação de captura de lead vindo de campanha/formulário externo

            Localização informada:
            Cidade: %s
            Estado: %s
            País: %s
            CEP: %s

            Nacionalidade: %s
            Data de registro na fonte externa: %s
            Telefone original: %s
            Celular original: %s
            """.formatted(
                city,
                state,
                country,
                postcode,
                user.nat() != null ? user.nat() : "não informado",
                registeredDate,
                user.phone() != null ? user.phone() : "não informado",
                user.cell() != null ? user.cell() : "não informado"
        );
    }
}