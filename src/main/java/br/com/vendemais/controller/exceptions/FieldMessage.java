package br.com.vendemais.controller.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a single invalid field entry returned when a CRM request payload
 * fails bean validation.
 */
@Schema(name = "FieldMessage", description = "Detalhe de validação para um campo específico do payload.")
public record FieldMessage (
        @Schema(example = "email")
        String fieldName,

        @Schema(example = "Formato de email inválido")
        String message
) {}
