package com.bootcamp.clientservice.port;

public interface AccountsClient {
    /** true si el cliente tiene al menos una cuenta activa */
    boolean hasAccounts(Long clientId);
}
