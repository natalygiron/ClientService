package com.bootcamp.clientservice.adapter;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.bootcamp.clientservice.dto.external.AccountResponse;
import com.bootcamp.clientservice.port.AccountsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccountsClient implements AccountsClient {

    private final RestTemplate restTemplate;

    @Value("${accounts.base-url:http://localhost:8081}")
    private String baseUrl;

    @Override
    public boolean hasAccounts(Long clientId) {
        String url = baseUrl + "/cuentas/" + clientId;

        try {
            ResponseEntity<List<AccountResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AccountResponse>>() {}
            );

            List<AccountResponse> accounts = response.getBody();
            boolean hasAccounts = response.getStatusCode().is2xxSuccessful() && accounts != null && !accounts.isEmpty();

            log.info("Checked accounts for client ID {}: found {} accounts", clientId, accounts != null ? accounts.size() : 0);
            return hasAccounts;

        } catch (RestClientException ex) {
            log.error("Error while checking accounts for client ID {}: {}", clientId, ex.getMessage(), ex);
            throw new IllegalStateException("No se pudo verificar las cuentas del cliente: " + clientId);
        }
    }
}
