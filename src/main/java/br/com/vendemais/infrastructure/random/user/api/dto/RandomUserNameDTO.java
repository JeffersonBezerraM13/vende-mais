package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserNameDTO(
        @JsonProperty("title")
        String title,

        @JsonProperty("first")
        String first,

        @JsonProperty("last")
        String last
) {
}