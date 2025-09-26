package com.bootcamp.clientservice.infrastructure.repository;

import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.domain.port.IClientRepository;
import com.bootcamp.clientservice.infrastructure.entity.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryJpaAdapter implements IClientRepository {

    private final ClientRepositoryJpa jpaRepository;

    @Override
    public Client save(Client client) {
        ClientEntity savedEntity = jpaRepository.save(ClientMapper.toEntity(client));
        return ClientMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(ClientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(ClientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByDni(String dni) {
        return jpaRepository.findByDni(dni).map(ClientMapper::toDomain);
    }

    @Override
    public boolean existsByDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(ClientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
