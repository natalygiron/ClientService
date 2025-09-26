package com.bootcamp.clientservice.domain.exception;

public final class ValidationMessages {
    private ValidationMessages() {}

    public static final String EMAIL_INVALID = "El correo electrónico tiene un formato inválido";
    public static final String DNI_INVALID = "El DNI debe tener entre 8 y 12 caracteres";
    public static final String FIELDS_REQUIRED = "Todos los campos son obligatorios";
}
