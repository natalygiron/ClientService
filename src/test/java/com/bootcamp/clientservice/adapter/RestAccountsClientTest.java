package com.bootcamp.clientservice.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.bootcamp.clientservice.dto.external.AccountResponse;
import com.bootcamp.clientservice.infrastructure.external.RestAccountsClient;

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
        ReflectionTestUtils.setField(client, "baseUrl", baseUrl);
    }

    @Test
    void hasAccounts_returns_true_when_accounts_exist() {
        String expectedUrl = baseUrl + "/cuentas/clientes/" + clientId;

        List<AccountResponse> accounts = List.of(new AccountResponse());
        ResponseEntity<List<AccountResponse>> response = new ResponseEntity<>(accounts, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenReturn(response);

        boolean result = client.hasAccounts(clientId);
        assertTrue(result);
    }

    @Test
    void hasAccounts_returns_false_when_no_accounts() {
        String expectedUrl = baseUrl + "/cuentas/clientes/" + clientId;

        ResponseEntity<List<AccountResponse>> response = new ResponseEntity<>(List.of(), HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenReturn(response);

        boolean result = client.hasAccounts(clientId);
        assertFalse(result);
    }

    @Test
    void hasAccounts_throws_exception_when_rest_client_fails() {
        String expectedUrl = baseUrl + "/cuentas/clientes/" + clientId;

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenThrow(new RestClientException("Connection error"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            client.hasAccounts(clientId);
        });

        assertEquals("No se pudo verificar las cuentas del cliente: " + clientId, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRestClientFails() {
        String expectedUrl = baseUrl + "/cuentas/clientes/" + clientId;

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenThrow(new RestClientException("Connection error"));

        assertThrows(IllegalStateException.class, () -> client.hasAccounts(clientId));
    }

    @Test
    void shouldReturnFalseWhenResponseIsNot2xxEvenIfAccountsExist() {
        String expectedUrl = baseUrl + "/cuentas/clientes/" + clientId;

        List<AccountResponse> accounts = List.of(new AccountResponse());
        ResponseEntity<List<AccountResponse>> response = new ResponseEntity<>(accounts, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<AccountResponse>>>any()))
                .thenReturn(response);

        boolean result = client.hasAccounts(clientId);
        assertFalse(result);
    }
}
