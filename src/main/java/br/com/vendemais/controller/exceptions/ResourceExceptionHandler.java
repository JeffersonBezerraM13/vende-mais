package br.com.vendemais.controller.exceptions;


import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralizes translation of business and validation exceptions into the API
 * error contract consumed by CRM clients.
 */
@ControllerAdvice
public class ResourceExceptionHandler {

    /**
     * Converts resource lookup failures into the standard 404 payload expected by
     * API consumers.
     *
     * @param ex business exception raised when the resource cannot be found
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized not-found payload
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFoundExeption
            (ObjectNotFoundException ex, HttpServletRequest request) {
        StandardError error = new StandardError(
                System.currentTimeMillis()
                , HttpStatus.NOT_FOUND.value()
                , "Object Not Found"
                ,ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Converts business integrity violations into a 400 payload that explains why
     * the requested CRM action could not be completed.
     *
     * @param ex business exception describing the violated rule
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized bad-request payload
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolationExeption
            (DataIntegrityViolationException ex, HttpServletRequest request) {
        StandardError error = new StandardError(
                System.currentTimeMillis()
                , HttpStatus.BAD_REQUEST.value()
                , "Violação de dados"
                ,ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Converts bean-validation failures into a detailed field error payload so
     * clients can correct invalid request bodies.
     *
     * @param ex validation exception produced by Spring MVC
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing field-level validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationError
            (MethodArgumentNotValidException ex, HttpServletRequest request) {
        ValidationError errors = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro na validação dos campos",
                "Campos inválidos",
                request.getRequestURI());
        for (FieldError fe: ex.getBindingResult().getFieldErrors()) {
            errors.addError(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
