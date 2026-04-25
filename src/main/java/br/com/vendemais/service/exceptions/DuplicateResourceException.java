package br.com.vendemais.service.exceptions;

/**
 * Signals that a request tries to create or update a resource using a value that
 * must remain unique in the CRM.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}