package br.com.vendemais.service.exceptions;

/**
 * Signals that a syntactically valid request violates a CRM business rule.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}