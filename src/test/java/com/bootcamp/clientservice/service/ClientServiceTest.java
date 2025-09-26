package com.bootcamp.clientservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bootcamp.clientservice.application.service.ClientService;
import com.bootcamp.clientservice.domain.exception.ClientNotFoundException;
import com.bootcamp.clientservice.domain.exception.DuplicateClientException;
import com.bootcamp.clientservice.domain.exception.ValidationException;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.domain.port.AccountsClient;
import com.bootcamp.clientservice.application.validation.ClientValidator;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    IClientRepository clientRepository;

    @Mock
    AccountsClient accountsClient;

    @Mock
    ClientValidator clientValidator;

    @InjectMocks
    ClientService service;

    // -------------------- register() --------------------

    @Test
    void register_ok() {
        CreateClientRequest req = CreateClientRequest.builder()
                .firstName("Ana")
                .lastName("Perez")
                .email("ana@mail.com")
                .dni("12345678")
                .build();

        Client saved = new Client(1L, "Ana", "Perez", "ana@mail.com", "12345678");

        doNothing().when(clientValidator).validateNewClient(any(Client.class));
        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        Client out = service.register(req);

        assertEquals(1L, out.getId());
        assertEquals("Ana", out.getFirstName());
        verify(clientValidator).validateNewClient(any(Client.class));
        verify(clientRepository).save(any(Client.class));
    }

    // -------------------- get() --------------------

    @Test
    void get_ok() {
        Client client = new Client(1L, "Ana", "Perez", "ana@mail.com", "12345678");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client out = service.get(1L);

        assertEquals("Ana", out.getFirstName());
        verify(clientRepository).findById(1L);
    }

    @Test
    void get_not_found_throws() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> service.get(99L));
        verify(clientRepository).findById(99L);
    }

    // -------------------- list() --------------------

    @Test
    void list_ok() {
        List<Client> clients = Arrays.asList(
                new Client(1L, "Ana", "Perez", "ana@mail.com", "12345678"),
                new Client(2L, "Luis", "Ramirez", "luis@mail.com", "87654321")
        );
        when(clientRepository.findAll()).thenReturn(clients);

        List<Client> out = service.list();

        assertEquals(2, out.size());
        verify(clientRepository).findAll();
    }

    // ---------- updateClient() OK ----------
    @Test
    void updateClient_ok_with_new_email_and_new_dni_not_duplicated() {
        Client existing = new Client(1L, "Ana", "P", "old@mail.com", "11111111");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(clientRepository.existsByDni("22222222")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client out = service.updateClient(1L, "Ana María", "Rodriguez", "new@mail.com", "22222222");

        assertEquals("Ana María", out.getFirstName());
        assertEquals("Rodriguez", out.getLastName());
        assertEquals("new@mail.com", out.getEmail());
        assertEquals("22222222", out.getDni());

        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByEmail("new@mail.com");
        verify(clientRepository).existsByDni("22222222");
        verify(clientRepository).save(any(Client.class));
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- email duplicado ----------
    @Test
    void updateClient_email_duplicated_throws() {
        Client existing = new Client(1L, "Ana", "P", "old@mail.com", "11111111");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmail("dup@mail.com")).thenReturn(true);

        var ex = assertThrows(DuplicateClientException.class,
                () -> service.updateClient(1L, "Ana", "P", "dup@mail.com", "11111111"));

        assertTrue(ex.getMessage().contains("email"));
        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByEmail("dup@mail.com");
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- dni duplicado ----------
    @Test
    void updateClient_dni_duplicated_throws() {
        Client existing = new Client(1L, "Ana", "P", "old@mail.com", "11111111");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByDni("22222222")).thenReturn(true);

        var ex = assertThrows(DuplicateClientException.class,
                () -> service.updateClient(1L, "Ana", "P", "old@mail.com","22222222"));

        assertTrue(ex.getMessage().contains("dni"));
        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByDni("22222222");
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- cliente no encontrado ----------
    @Test
    void updateClient_not_found_throws() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        var ex = assertThrows(ClientNotFoundException.class,
                () -> service.updateClient(99L, "A", "B", "c@d.com", "11111111"));

        assertTrue(ex.getMessage().contains("99"));
        verify(clientRepository).findById(99L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // -------------------- deleteClient() --------------------

    @Test
    void deleteClient_ok() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(accountsClient.hasAccounts(1L)).thenReturn(false);
        doNothing().when(clientRepository).deleteById(1L);

        service.deleteClient(1L);

        verify(clientRepository).existsById(1L);
        verify(accountsClient).hasAccounts(1L);
        verify(clientRepository).deleteById(1L);
    }

    // ---------- cliente no encontrado ----------
    @Test
    void deleteClient_not_found_throws() {
        when(clientRepository.existsById(99L)).thenReturn(false);

        assertThrows(ClientNotFoundException.class, () -> service.deleteClient(99L));

        verify(clientRepository).existsById(99L);
    }

    // ---------- cliente con cuentas activas error ----------
    @Test
    void deleteClient_with_accounts_throws() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(accountsClient.hasAccounts(1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> service.deleteClient(1L));

        verify(clientRepository).existsById(1L);
        verify(accountsClient).hasAccounts(1L);
    }
}
