package br.com.vendemais.service.exceptions;

/**
 * Signals that a requested CRM resource does not exist or is not reachable in
 * the expected context.
 */
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
