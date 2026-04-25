package br.com.vendemais.controller.exceptions;

import br.com.vendemais.service.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralizes translation of business, validation, and domain exceptions into
 * the API error contract consumed by CRM clients.
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
    public ResponseEntity<StandardError> objectNotFoundException(
            ObjectNotFoundException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Converts duplicated resource attempts into a 409 response, indicating that
     * the request conflicts with the current persisted state.
     *
     * @param ex exception describing the duplicated resource constraint
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized conflict payload
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<StandardError> duplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Converts valid requests that violate business rules into a 422 response.
     *
     * @param ex exception describing the violated business rule
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized business rule payload
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<StandardError> businessRuleException(
            BusinessRuleException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Regra de negócio violada",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Converts legacy business integrity violations into a 400 payload. This is
     * kept for compatibility while services are gradually migrated to more
     * specific domain exceptions.
     *
     * @param ex business exception describing the violated rule
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized bad-request payload
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Violação de dados",
                ex.getMessage(),
                request.getRequestURI()
        );

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
    public ResponseEntity<StandardError> validationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        ValidationError errors = new ValidationError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro na validação dos campos",
                "Campos inválidos",
                request.getRequestURI()
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Converts validation failures from request parameters, path variables and
     * other method-level constraints into a 400 payload.
     *
     * @param ex validation exception produced by method-level constraints
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized validation payload
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> constraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro na validação dos parâmetros",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Converts invalid method arguments that are rejected before reaching deeper
     * business validation into a 400 payload.
     *
     * @param ex exception describing the invalid argument
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized bad-request payload
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro inválido",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Converts attempts to remove resources still referenced by related records into
     * a 409 response.
     *
     * @param ex exception describing why the resource cannot be removed
     * @param request current HTTP request used to populate the failing path
     * @return a response entity containing the standardized conflict payload
     */
    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<StandardError> resourceInUseException(
            ResourceInUseException ex,
            HttpServletRequest request
    ) {
        StandardError error = new StandardError(
                System.currentTimeMillis(),
                HttpStatus.CONFLICT.value(),
                "Conflito de dados",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
