package br.com.vendemais.controller.exceptions;

public record FieldMessage (
        String fieldName,
        String message
) {}
