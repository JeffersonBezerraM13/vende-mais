package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserLocationDTO(
        @JsonProperty("street")
        RandomUserStreetDTO street,

        @JsonProperty("city")
        String city,

        @JsonProperty("state")
        String state,

        @JsonProperty("country")
        String country,

        @JsonProperty("postcode")
        String postcode
) {
}