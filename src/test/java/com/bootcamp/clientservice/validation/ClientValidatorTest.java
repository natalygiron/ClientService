package com.bootcamp.clientservice.validation;

import com.bootcamp.clientservice.application.validation.ClientValidator;
import com.bootcamp.clientservice.domain.exception.DuplicateClientException;
import com.bootcamp.clientservice.domain.exception.ValidationException;
import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientValidatorTest {

    @Mock
    IClientRepository repo;

    @InjectMocks
    ClientValidator validator;

    @Test
    void validateNewClient_whenMissingFields_thenThrowValidationException() {
        Client client = new Client(null, "", "Lastname", "", "");
        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Todos los campos");
    }

    @Test
    void validateNewClient_whenDniDuplicate_thenThrowDuplicate() {
        Client client = new Client(null, "A", "B", "a@b.com", "12345678");
        when(repo.existsByDni("12345678")).thenReturn(true);
        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(DuplicateClientException.class)
                .hasMessageContaining("DNI");
    }

    @Test
    void validateNewClient_whenEmailDuplicate_thenThrowDuplicate() {
        Client client = new Client(null, "A", "B", "a@b.com", "12345678");
        when(repo.existsByDni("12345678")).thenReturn(false);
        when(repo.existsByEmail("a@b.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(DuplicateClientException.class)
                .hasMessageContaining("email");
    }

    @Test
    void validateNewClient_whenEmailInvalid_thenThrowValidationException() {
        Client client = new Client(null, "A", "B", "invalid-email", "12345678");
        when(repo.existsByDni("12345678")).thenReturn(false);
        when(repo.existsByEmail("invalid-email")).thenReturn(false);

        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("correo electrónico");
    }

    @Test
    void validateNewClient_whenDniTooShort_thenThrowValidationException() {
        Client client = new Client(null, "A", "B", "a@b.com", "123"); // < 8
        when(repo.existsByDni("123")).thenReturn(false);
        when(repo.existsByEmail("a@b.com")).thenReturn(false);

        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("DNI");
    }

    @Test
    void validateNewClient_whenDniTooLong_thenThrowValidationException() {
        Client client = new Client(null, "A", "B", "a@b.com", "1234567890123"); // > 12
        when(repo.existsByDni(any())).thenReturn(false);
        when(repo.existsByEmail(any())).thenReturn(false);

        assertThatThrownBy(() -> validator.validateNewClient(client))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("DNI");
    }

    @Test
    void validateNewClient_whenAllGood_thenNoException() {
        Client client = new Client(null, "A", "B", "a@b.com", "12345678");
        when(repo.existsByDni(any())).thenReturn(false);
        when(repo.existsByEmail(any())).thenReturn(false);

        // no debe lanzar excepción
        validator.validateNewClient(client);
        verify(repo).existsByDni("12345678");
        verify(repo).existsByEmail("a@b.com");
    }

    // ------------------- validateUpdateClient -------------------

    @Test
    void validateUpdateClient_whenEmailChangedAndExists_thenThrowDuplicate() {
        Client current = new Client(1L, "A", "B", "old@example.com", "12345678");
        when(repo.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdateClient(current, "new@example.com", "12345678"))
                .isInstanceOf(DuplicateClientException.class)
                .hasMessageContaining("email");
    }

    @Test
    void validateUpdateClient_whenDniChangedAndExists_thenThrowDuplicate() {
        Client current = new Client(1L, "A", "B", "a@b.com", "11111111");
        when(repo.existsByDni("22222222")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdateClient(current, "a@b.com", "22222222"))
                .isInstanceOf(DuplicateClientException.class)
                .hasMessageContaining("dni");
    }

    @Test
    void validateUpdateClient_whenNoChanges_thenNoException() {
        Client current = new Client(1L, "A", "B", "a@b.com", "12345678");

        // mismo email y dni → no consulta al repo
        validator.validateUpdateClient(current, "a@b.com", "12345678");

        verify(repo, never()).existsByEmail(any());
        verify(repo, never()).existsByDni(any());
    }
}
