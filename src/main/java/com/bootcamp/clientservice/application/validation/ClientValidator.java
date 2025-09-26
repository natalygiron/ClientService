package com.bootcamp.clientservice.domain.validation;

import javax.validation.ValidationException;
import com.bootcamp.clientservice.domain.exception.DuplicateClientException;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;

public class ClientValidator {

    private final IClientRepository clientRepository;

    public ClientValidator(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void validateNewClient(Client client) {
        if (isBlank(client.getFirstName()) || isBlank(client.getLastName()) ||
                isBlank(client.getDni()) || isBlank(client.getEmail())) {
            throw new ValidationException("Todos los campos son obligatorios");
        }

        if (clientRepository.existsByDni(client.getDni())) {
            throw new DuplicateClientException("DNI", client.getDni());
        }

        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new DuplicateClientException("email", client.getEmail());
        }

        if (!isValidEmail(client.getEmail())) {
            throw new ValidationException("El correo electrónico tiene un formato inválido");
        }

        if (client.getDni().length() < 8 || client.getDni().length() > 12) {
            throw new ValidationException("El DNI debe tener entre 8 y 12 caracteres");
        }

    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }
}
