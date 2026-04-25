package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserRegisteredDTO(
        @JsonProperty("date")
        String date,

        @JsonProperty("age")
        Integer age
) {
}