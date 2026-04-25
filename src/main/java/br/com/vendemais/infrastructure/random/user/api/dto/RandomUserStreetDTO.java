package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserStreetDTO(
        @JsonProperty("number")
        Integer number,

        @JsonProperty("name")
        String name
) {
}