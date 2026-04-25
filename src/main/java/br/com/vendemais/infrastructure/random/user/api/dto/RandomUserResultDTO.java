package br.com.vendemais.infrastructure.random.user.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RandomUserResultDTO(
        @JsonProperty("gender")
        String gender,

        @JsonProperty("name")
        RandomUserNameDTO name,

        @JsonProperty("location")
        RandomUserLocationDTO location,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("cell")
        String cell,

        @JsonProperty("registered")
        RandomUserRegisteredDTO registered,

        @JsonProperty("nat")
        String nat
) {
}