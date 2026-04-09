package br.com.vendemais.service.exceptions;

/**
 * Signals that a request violates a business or relational integrity rule
 * enforced by the CRM.
 */
public class DataIntegrityViolationException extends RuntimeException {
    public DataIntegrityViolationException(String message) {
        super(message);
    }

    public DataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
