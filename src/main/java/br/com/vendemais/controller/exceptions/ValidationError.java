package br.com.vendemais.controller.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the standard API error payload with field-level violations produced by
 * request validation.
 */
@Schema(name = "ValidationError", description = "Formato de erro retornado quando o payload da requisicao e invalido.")
public class ValidationError extends StandardError{

    @Schema(example = "[{\"fieldName\":\"email\",\"message\":\"Formato de email invalido\"}]")
    private List<FieldMessage> erros = new ArrayList<>();

    public ValidationError(Long timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    /**
     * Registers an invalid field so clients can identify which payload entries
     * must be corrected before retrying the request.
     *
     * @param fieldName name of the field rejected during validation
     * @param message validation message explaining why the field is invalid
     */
    public void addError(String fieldName, String message) {
        this.erros.add(new FieldMessage(fieldName, message));
    }

    public List<FieldMessage> getErros() {
        return erros;
    }
}
