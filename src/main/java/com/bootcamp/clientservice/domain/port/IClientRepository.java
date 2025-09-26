package com.bootcamp.clientservice.domain.port;

import java.util.List;
import java.util.Optional;
import com.bootcamp.clientservice.domain.model.Client;

public interface IClientRepository {
    Client save(Client client);

    Optional<Client> findById(Long id);

    Optional<Client> findByEmail(String email); // Evitar null, manejo de ausencia de datos
    Optional<Client> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    List<Client> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
