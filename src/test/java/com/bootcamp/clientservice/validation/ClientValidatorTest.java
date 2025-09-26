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
        Client client = new Client(null, "", "Lastname", "", ""); // bad fields
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
    void validateNewClient_whenAllGood_thenNoException() {
        Client client = new Client(null, "A", "B", "a@b.com", "12345678");
        when(repo.existsByDni(any())).thenReturn(false);
        when(repo.existsByEmail(any())).thenReturn(false);
        // shouldn't throw
        validator.validateNewClient(client);
        verify(repo).existsByDni("12345678");
    }
}
