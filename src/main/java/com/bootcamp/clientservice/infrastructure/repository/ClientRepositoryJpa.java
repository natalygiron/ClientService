package com.bootcamp.clientservice.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.bootcamp.clientservice.infrastructure.entity.ClientEntity;

public interface ClientRepositoryJpa extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByEmail(String email);
    Optional<ClientEntity> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
