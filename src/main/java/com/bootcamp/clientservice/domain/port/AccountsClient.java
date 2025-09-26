package com.bootcamp.clientservice.domain.port;

public interface IAccountsClient {
    /** true si el cliente tiene al menos una cuenta activa */
    boolean hasAccounts(Long clientId);
}
