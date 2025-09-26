package com.bootcamp.clientservice.application.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bootcamp.clientservice.domain.exception.ClientNotFoundException;
import com.bootcamp.clientservice.domain.exception.DuplicateClientException;
import com.bootcamp.clientservice.domain.exception.ValidationException;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.domain.port.AccountsClient;
import com.bootcamp.clientservice.application.validation.ClientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ClientService {

    private final IClientRepository clientRepository;
    private final AccountsClient accountsClient; // <<— DIP
    private final ClientValidator clientValidator; // <<- S — Single Responsibility

    @Transactional
    public Client register(CreateClientRequest req) {
        Client client = new Client(
                req.getFirstName(),
                req.getLastName(),
                req.getEmail(),
                req.getDni()
        );

        log.info("Attempting to register client with DNI: {}", client.getDni());
        clientValidator.validateNewClient(client);
        Client saved = clientRepository.save(client);
        log.info("Client registered successfully with ID: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Client get(Long id) {
        log.info("Fetching client with ID: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found with ID: {}", id);
                    return new ClientNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<Client> list() {
        log.info("Listing all clients");
        return clientRepository.findAll();
    }

    @Transactional
    public Client updateClient(Long id, String firstName, String lastName, String email, String dni) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found for update. ID: {}", id);
                    return new ClientNotFoundException(id);
                });

        String newFirstName = Optional.ofNullable(firstName)
                .filter(s -> !s.isBlank()).map(String::trim)
                .orElse(client.getFirstName());

        String newLastName = Optional.ofNullable(lastName)
                .filter(s -> !s.isBlank()).map(String::trim)
                .orElse(client.getLastName());

        String newEmail = Optional.ofNullable(email)
                .filter(s -> !s.isBlank()).map(String::trim)
                .orElse(client.getEmail());

        String newDni = Optional.ofNullable(dni)
                .filter(s -> !s.isBlank()).map(String::trim)
                .orElse(client.getDni());

        // Validar duplicado de email solo si realmente cambia
        if (!newEmail.equalsIgnoreCase(client.getEmail()) && clientRepository.existsByEmail(newEmail)) {
            log.warn("Email already in use: {}", newEmail);
            throw new DuplicateClientException("email", newEmail);
        }

        // Validar duplicado de DNI solo si realmente cambia
        if (!newDni.equalsIgnoreCase(client.getDni()) && clientRepository.existsByDni(newDni)) {
            log.warn("DNI already in use: {}", newDni);
            throw new DuplicateClientException("dni", newDni);
        }

        Client updated = new Client(
                client.getId(),
                newFirstName,
                newLastName,
                newEmail,
                newDni
        );

        Client saved = clientRepository.save(updated);
        log.info("Client updated successfully. ID: {}", updated.getId());
        return saved;
    }

    @Transactional
    public void deleteClient(Long id) {
        log.info("Attempting to delete client with ID: {}", id);
        if (!clientRepository.existsById(id)) {
            log.warn("Client not found for deletion. ID: {}", id);
            throw new ClientNotFoundException(id);
        }

        if (accountsClient.hasAccounts(id)) { // <<-- Usando el puerto
            log.warn("Client has active accounts. Cannot delete. ID: {}", id);
            throw new ValidationException("Cannot delete client with active accounts");
        }

        clientRepository.deleteById(id);
        log.info("Client deleted successfully. ID: {}", id);
    }
}
