package com.bootcamp.clientservice.infrastructure.repository;

import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.infrastructure.entity.ClientEntity;

public class ClientMapper {

    public static ClientEntity toEntity(Client client) {
        return ClientEntity.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .dni(client.getDni())
                .build();
    }

    public static Client toDomain(ClientEntity entity) {
        return new Client(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getDni()
        );
    }
}

