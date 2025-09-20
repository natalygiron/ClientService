package com.bootcamp.clientservice.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.bootcamp.clientservice.dto.external.AccountResponse;

@ExtendWith(MockitoExtension.class)
class RestAccountsClientTest {

    @Mock
    RestTemplate restTemplate;

    private RestAccountsClient client;

    private String baseUrl = "http://localhost:8081";
    private final Long clientId = 2L;

    @BeforeEach
    void setup() {
        client = new RestAccountsClient(restTemplate);
        // Inyecta el valor directamente en el campo baseUrl antes de ejecutar los tests.
        ReflectionTestUtils.setField(client, "baseUrl", baseUrl);
    }

    @Test
    void hasAccounts_returns_true_when_accounts_exist() {
//        Long clientId = 1L;
        String url = "http://localhost:8081/cuentas/" + clientId;

        List<AccountResponse> accounts = List.of(new AccountResponse());
        ResponseEntity<List<AccountResponse>> response = new ResponseEntity<>(accounts, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenReturn(response);

        boolean result = client.hasAccounts(clientId);
        assertTrue(result);
    }

    @Test
    void hasAccounts_returns_false_when_no_accounts() {
//        Long clientId = 2L;
        String url = baseUrl + "/cuentas/" + clientId;

        ResponseEntity<List<AccountResponse>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenReturn(response);

        boolean result = client.hasAccounts(clientId);
        assertFalse(result);
    }

    @Test
    void hasAccounts_throws_exception_when_rest_client_fails() {
//        Long clientId = 3L;
        String url = baseUrl + "/cuentas/" + clientId;

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenThrow(new RestClientException("Connection error"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            client.hasAccounts(clientId);
        });

        assertEquals("No se pudo verificar las cuentas del cliente: " + clientId, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRestClientFails() {
//        Long clientId = 1L;
        // Arrange
        Mockito.when(restTemplate.exchange(
                eq(baseUrl + "/cuentas/" + clientId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RestClientException("Connection error"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            client.hasAccounts(clientId);
        });

        assertEquals("No se pudo verificar las cuentas del cliente: " + clientId, exception.getMessage());
    }

    @Test
    void shouldReturnFalseWhenResponseIsNot2xxEvenIfAccountsExist() {
        // Arrange
        List<AccountResponse> accounts = List.of(
                AccountResponse.builder().id(1L).accountNumber("ACC123").balance(500.0).clientId(clientId).build()
        );

        ResponseEntity<List<AccountResponse>> responseEntity = new ResponseEntity<>(accounts, HttpStatus.BAD_REQUEST);

        Mockito.when(restTemplate.exchange(
                eq(baseUrl + "/cuentas/" + clientId),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(responseEntity);

        // Act
        boolean result = client.hasAccounts(clientId);

        // Assert
        assertFalse(result, "Debe retornar false si el status no es 2xx");
    }


}
