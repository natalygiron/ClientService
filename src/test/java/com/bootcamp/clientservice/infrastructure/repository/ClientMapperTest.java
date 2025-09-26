package com.bootcamp.clientservice.infrastructure.repository;

import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.infrastructure.entity.ClientEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests para ClientMapper.
 * Protege contra regresiones en el mapeo entre dominio <-> entidad JPA.
 */
class ClientMapperTest {

    @Test
    @DisplayName("toEntity: debe mapear correctamente domain -> entity (orden y valores)")
    void toEntity_shouldMapDomainToEntity() {
        // given - un cliente de dominio con valores claros
        Client domain = new Client(1L, "John", "Doe", "john@example.com", "12345678");

        // when - mapear a entity
        ClientEntity entity = ClientMapper.toEntity(domain);

        // then - todos los campos deben mapearse en el lugar correcto
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getEmail()).isEqualTo("john@example.com");
        assertThat(entity.getDni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("toDomain: debe mapear correctamente entity -> domain (orden y valores)")
    void toDomain_shouldMapEntityToDomain() {
        // given - entidad JPA con valores
        ClientEntity entity = new ClientEntity(2L, "Jane", "Smith", "jane@example.com", "87654321");

        // when - mapear a domain
        Client domain = ClientMapper.toDomain(entity);

        // then - campos correctamente colocados
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getFirstName()).isEqualTo("Jane");
        assertThat(domain.getLastName()).isEqualTo("Smith");
        assertThat(domain.getEmail()).isEqualTo("jane@example.com");
        assertThat(domain.getDni()).isEqualTo("87654321");
    }
}
