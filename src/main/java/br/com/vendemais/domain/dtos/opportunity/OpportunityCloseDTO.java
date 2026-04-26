package br.com.vendemais.domain.dtos.opportunity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Carries the win/loss decision and optional loss reason used to close an
 * opportunity.
 */
@Schema(name = "OpportunityCloseDTO", description = "Payload usado no fechamento de uma oportunidade.")
public record OpportunityCloseDTO(
        @NotNull(message = "É obrigatório informar se a oportunidade foi ganha (true) ou perdida (false)")
        @Schema(example = "false")
        Boolean win,

        // Se for win = false, o lossReason deveria ser preenchido (você pode validar isso no Service!)
        @Schema(example = "Blue Corp adiou a contratação para o próximo trimestre.")
        String lossReason
) {}
