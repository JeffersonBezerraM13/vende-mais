package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserApiResponseDTO(
        @JsonProperty("results")
        List<RandomUserResultDTO> results
) {
}