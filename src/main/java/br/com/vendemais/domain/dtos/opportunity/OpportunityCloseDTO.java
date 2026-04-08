package br.com.vendemais.domain.dtos.opportunity;

import jakarta.validation.constraints.NotNull;

public record OpportunityCloseDTO(
        @NotNull(message = "É obrigatório informar se a oportunidade foi ganha (true) ou perdida (false)")
        Boolean win,

        // Se for win = false, o lossReason deveria ser preenchido (você pode validar isso no Service!)
        String lossReason
) {}