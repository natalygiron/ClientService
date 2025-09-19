package com.bootcamp.clientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bootcamp.clientservice.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email); // Evitar null, manejo de ausencia de datos
    Optional<Client> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
