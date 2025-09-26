package com.bootcamp.clientservice.application.validation;

import static com.bootcamp.clientservice.domain.exception.ValidationMessages.*;
import org.springframework.stereotype.Component;
import com.bootcamp.clientservice.domain.exception.DuplicateClientException;
import com.bootcamp.clientservice.domain.exception.ValidationException;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final IClientRepository clientRepository;

    public void validateNewClient(Client client) {
        // Validar campos obligatorios con Stream
        if (Stream.of(client.getFirstName(), client.getLastName(), client.getDni(), client.getEmail())
                .anyMatch(this::isBlank)) {
            throw new ValidationException(FIELDS_REQUIRED);
        }

        // Validar duplicados usando repositorio
        if (clientRepository.existsByDni(client.getDni())) {
            throw new DuplicateClientException("DNI", client.getDni());
        }

        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new DuplicateClientException("email", client.getEmail());
        }

        // Validar formato email
        if (!isValidEmail(client.getEmail())) {
            throw new ValidationException(EMAIL_INVALID);
        }

        // Validar longitud de DNI
        if (client.getDni().length() < 8 || client.getDni().length() > 12) {
            throw new ValidationException(DNI_INVALID);
        }
    }

    public void validateUpdateClient(Client currentClient, String newEmail, String newDni) {
        if (!newEmail.equalsIgnoreCase(currentClient.getEmail()) &&
                clientRepository.existsByEmail(newEmail)) {
            throw new DuplicateClientException("email", newEmail);
        }

        if (!newDni.equalsIgnoreCase(currentClient.getDni()) &&
                clientRepository.existsByDni(newDni)) {
            throw new DuplicateClientException("dni", newDni);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }
}
