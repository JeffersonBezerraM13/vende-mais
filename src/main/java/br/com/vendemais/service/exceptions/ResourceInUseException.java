package br.com.vendemais.service.exceptions;

/**
 * Signals that a resource cannot be removed because it is still referenced by
 * related CRM records.
 */
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}