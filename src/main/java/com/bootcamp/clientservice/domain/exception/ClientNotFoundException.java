package com.bootcamp.clientservice.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Cliente no encontrado id: " + id);
    }
}
