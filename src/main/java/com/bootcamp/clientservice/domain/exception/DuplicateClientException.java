package com.bootcamp.clientservice.domain.exception;

public class DuplicateClientException extends RuntimeException {
    public DuplicateClientException(String field, String value) {
        super(String.format("Cliente duplicado con %s: %s", field, value));
    }
}

