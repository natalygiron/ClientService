package com.bootcamp.clientservice.service;

import java.util.List;
import java.util.function.Consumer;
import javax.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bootcamp.clientservice.domain.Client;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.port.AccountsClient;
import com.bootcamp.clientservice.repository.ClientRepository;
import com.bootcamp.clientservice.validation.ClientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AccountsClient accountsClient; // <<— DIP
    private final ClientValidator clientValidator; // <<- S — Single Responsibility

    @Transactional
    public Client register(CreateClientRequest req) {
        Client client = Client.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .dni(req.getDni())
                .build();

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
                    return new IllegalArgumentException("Client not found");
                });
    }

    @Transactional(readOnly = true)
    public List<Client> list() {
        log.info("Listing all clients");
        return clientRepository.findAll();
    }

    @Transactional
    public Client updateClient(Long id, String firstName, String lastName, String email) {
        log.info("Updating client with ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found for update. ID: {}", id);
                    return new IllegalArgumentException("Client with ID " + id + " not found");
                });

        updateIfPresent(client::setFirstName, firstName);
        updateIfPresent(client::setLastName, lastName);

        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(client.getEmail())) {
            if (clientRepository.existsByEmail(email)) {
                log.warn("Email already in use: {}", email);
                throw new IllegalArgumentException("Email is already in use");
            }
            client.setEmail(email);
        }

        Client updated = clientRepository.save(client);
        log.info("Client updated successfully. ID: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void deleteClient(Long id) {
        log.info("Attempting to delete client with ID: {}", id);
        if (!clientRepository.existsById(id)) {
            log.warn("Client not found for deletion. ID: {}", id);
            throw new IllegalArgumentException("Client not found");
        }

        if (accountsClient.hasAccounts(id)) { // <<-- Usando el puerto
            log.warn("Client has active accounts. Cannot delete. ID: {}", id);
            throw new ValidationException("Cannot delete client with active accounts");
        }

        clientRepository.deleteById(id);
        log.info("Client deleted successfully. ID: {}", id);
    }

    private void updateIfPresent(Consumer<String> setter, String value) {
        if (value != null && !value.isBlank()) setter.accept(value.trim());
    }
}
