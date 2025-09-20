package com.bootcamp.clientservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bootcamp.clientservice.domain.Client;
import com.bootcamp.clientservice.dto.request.CreateClientRequest;
import com.bootcamp.clientservice.port.AccountsClient;
import com.bootcamp.clientservice.repository.ClientRepository;
import com.bootcamp.clientservice.validation.ClientValidator;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    AccountsClient accountsClient;

    @Mock
    ClientValidator clientValidator;

    @InjectMocks
    ClientService service;

    // ---------- register() ----------
    @Test
    void register_ok() {
        CreateClientRequest req = new CreateClientRequest();
        req.setFirstName("Ana");
        req.setLastName("Perez");
        req.setDni("12345678");
        req.setEmail("ana@mail.com");

        Client client = Client.builder()
                .firstName("Ana").lastName("Perez")
                .dni("12345678").email("ana@mail.com")
                .build();
        // Validacion exacta usando client. Alternativa any(Client.class)
        doNothing().when(clientValidator).validateNewClient(eq(client));
        when(clientRepository.save(any(Client.class))).thenReturn(
                Client.builder().id(1L).firstName("Ana").lastName("Perez")
                        .dni("12345678").email("ana@mail.com").build()
        );

        Client out = service.register(req);

        assertEquals(1L, out.getId());
        verify(clientValidator).validateNewClient(any(Client.class));
        verify(clientRepository).save(any(Client.class));
        verifyNoMoreInteractions(clientRepository, accountsClient);
    }

    @Test
    void register_fails_when_required_fields_blank() {
        CreateClientRequest req = new CreateClientRequest();

        // Validación delegada, simula el comportamiento del ClientValidator
        doThrow(new ValidationException("All fields are required"))
                .when(clientValidator).validateNewClient(any(Client.class));

        var ex = assertThrows(ValidationException.class, () -> service.register(req));
        assertTrue(ex.getMessage().contains("All fields are required"));
        verify(clientValidator).validateNewClient(any(Client.class));
        verifyNoInteractions(clientRepository, accountsClient);
    }

    @Test
    void register_fails_when_dni_duplicated() {
        CreateClientRequest req = new CreateClientRequest();
        req.setFirstName("Ana"); req.setLastName("Perez");
        req.setDni("123"); req.setEmail("a@a.com");

        // Validación delegada, simula el comportamiento del ClientValidator
        doThrow(new IllegalArgumentException("Dni is already in use"))
                .when(clientValidator).validateNewClient(any(Client.class));

        var ex = assertThrows(IllegalArgumentException.class, () -> service.register(req));
        assertTrue(ex.getMessage().contains("Dni is already in use"));
        verify(clientValidator).validateNewClient(any(Client.class));
        verifyNoInteractions(clientRepository, accountsClient);
    }

    @Test
    void register_fails_when_email_duplicated() {
        CreateClientRequest req = new CreateClientRequest();
        req.setFirstName("Ana"); req.setLastName("Perez");
        req.setDni("123"); req.setEmail("a@a.com");

        // Validación delegada, simula el comportamiento del ClientValidator
        doThrow(new IllegalArgumentException("Email is already in use"))
                .when(clientValidator).validateNewClient(any(Client.class));

        var ex = assertThrows(IllegalArgumentException.class, () -> service.register(req));
        assertTrue(ex.getMessage().contains("Email is already in use"));
        verify(clientValidator).validateNewClient(any(Client.class));
        verifyNoInteractions(clientRepository, accountsClient);
    }

    // ---------- get() ----------
    @Test
    void get_found() {
        Client c = Client.builder().id(7L).firstName("Ana").build();
        when(clientRepository.findById(7L)).thenReturn(Optional.of(c));

        Client out = service.get(7L);

        assertEquals(7L, out.getId());
        verify(clientRepository).findById(7L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    @Test
    void get_not_found() {
        when(clientRepository.findById(7L)).thenReturn(Optional.empty());
        var ex = assertThrows(IllegalArgumentException.class, () -> service.get(7L));
        assertTrue(ex.getMessage().contains("Client not found"));
        verify(clientRepository).findById(7L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- list() ----------
    @Test
    void list_ok() {
        when(clientRepository.findAll()).thenReturn(List.of(
                Client.builder().id(1L).build(),
                Client.builder().id(2L).build()
        ));

        var list = service.list();
        assertEquals(2, list.size());
        verify(clientRepository).findAll();
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- updateClient() ----------
    @Test
    void updateClient_ok_with_new_email_not_duplicated() {
        Client existing = Client.builder()
                .id(1L).firstName("Ana").lastName("P")
                .email("old@mail.com").build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client out = service.updateClient(1L, "Ana María", "Perez", "new@mail.com");

        assertEquals("Ana María", out.getFirstName());
        assertEquals("Perez", out.getLastName());
        assertEquals("new@mail.com", out.getEmail());
        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByEmail("new@mail.com");
        verify(clientRepository).save(existing);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    @Test
    void updateClient_email_duplicated_throws() {
        Client existing = Client.builder()
                .id(1L).firstName("Ana").lastName("P")
                .email("old@mail.com").build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.existsByEmail("dup@mail.com")).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateClient(1L, "Ana", "P", "dup@mail.com"));
        assertTrue(ex.getMessage().contains("Email is already in use"));
        verify(clientRepository).findById(1L);
        verify(clientRepository).existsByEmail("dup@mail.com");
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    @Test
    void updateClient_not_found_throws() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateClient(99L, "A", "B", "c@d.com"));
        assertTrue(ex.getMessage().contains("not found"));
        verify(clientRepository).findById(99L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    // ---------- deleteClient() ----------
    @Test
    void deleteClient_ok_when_no_accounts() {
        when(clientRepository.existsById(10L)).thenReturn(true);
        when(accountsClient.hasAccounts(10L)).thenReturn(false);

        service.deleteClient(10L);

        verify(clientRepository).existsById(10L);
        verify(accountsClient).hasAccounts(10L);
        verify(clientRepository).deleteById(10L);
        verifyNoMoreInteractions(clientRepository, accountsClient);
    }

    @Test
    void deleteClient_not_found_throws() {
        when(clientRepository.existsById(10L)).thenReturn(false);
        var ex = assertThrows(IllegalArgumentException.class, () -> service.deleteClient(10L));
        assertTrue(ex.getMessage().contains("Client not found"));
        verify(clientRepository).existsById(10L);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(accountsClient);
    }

    @Test
    void deleteClient_with_active_accounts_throws() {
        when(clientRepository.existsById(10L)).thenReturn(true);
        when(accountsClient.hasAccounts(10L)).thenReturn(true);

        var ex = assertThrows(ValidationException.class, () -> service.deleteClient(10L));
        assertTrue(ex.getMessage().contains("Cannot delete client with active accounts"));
        verify(clientRepository).existsById(10L);
        verify(accountsClient).hasAccounts(10L);
        verifyNoMoreInteractions(clientRepository, accountsClient);
    }

    @Test
    void updateClient_should_not_update_email_when_same_as_current() {
        Client client = new Client();
        client.setId(1L);
        client.setEmail("same@example.com");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenReturn(client);

        Client result = service.updateClient(1L, null, null, "same@example.com");

        assertEquals("same@example.com", result.getEmail());
        verify(clientRepository, never()).existsByEmail(any());
    }
}
