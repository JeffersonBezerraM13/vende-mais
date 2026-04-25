package br.com.vendemais.infrastructure.random.user.api;

import br.com.vendemais.infrastructure.random.user.api.dto.RandomUserApiResponseDTO;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * HTTP client responsible for consuming the external Random User API used to
 * simulate marketing lead capture.
 */
@Component
public class RandomUserApiClient {

    private final RestTemplate restTemplate;

    @Value("${vende.mais.integration.random.user.api.url:https://randomuser.me}")
    private String domain;

    @Value("${vende.mais.integration.random.user.api.path:/api/}")
    private String path;

    public RandomUserApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches Brazilian random user profiles from the external API according to
     * the requested quantity.
     *
     * @param quantity number of external profiles to request
     * @return the external API response, when available
     * @throws DataIntegrityViolationException if the API configuration or quantity is invalid
     */
    public Optional<RandomUserApiResponseDTO> getRandomUsers(Integer quantity) {
        if (domain == null || domain.isBlank() || !domain.startsWith("http")) {
            throw new DataIntegrityViolationException("URL da Random User API não configurada corretamente.");
        }

        if (quantity == null || quantity < 1) {
            throw new DataIntegrityViolationException("A quantidade de usuários deve ser maior que zero.");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl(domain)
                .path(path)
                .queryParam("results", quantity)
                .queryParam("nat", "br")
                .queryParam("inc", "name,email,phone,cell,location,registered,nat")
                .toUriString();

        RandomUserApiResponseDTO response = restTemplate.getForObject(
                url,
                RandomUserApiResponseDTO.class
        );

        return Optional.ofNullable(response);
    }
}