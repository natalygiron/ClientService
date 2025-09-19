package com.bootcamp.clientservice.validation;

import static org.apache.logging.log4j.util.Strings.isBlank;
import javax.validation.ValidationException;
import org.springframework.stereotype.Component;
import com.bootcamp.clientservice.domain.Client;
import com.bootcamp.clientservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final ClientRepository clientRepository;

    public void validateNewClient(Client client) {
        if (isBlank(client.getFirstName()) || isBlank(client.getLastName()) ||
                isBlank(client.getDni()) || isBlank(client.getEmail())) {
            throw new ValidationException("Todos los campos son obligatorios");
        }

        if (clientRepository.existsByDni(client.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado");
        }

        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        if (!isValidEmail(client.getEmail())) {
            throw new ValidationException("El correo electrónico tiene un formato inválido");
        }

        if (client.getDni().length() < 8 || client.getDni().length() > 12) {
            throw new ValidationException("El DNI debe tener entre 8 y 12 caracteres");
        }

    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }
}
