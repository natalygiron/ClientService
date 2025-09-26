package com.bootcamp.clientservice.infrastructure.repository;

import com.bootcamp.clientservice.domain.model.Client;
import com.bootcamp.clientservice.infrastructure.entity.ClientEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryJpaAdapterTest {

    @Mock
    ClientRepositoryJpa jpaRepository;

    @InjectMocks
    ClientRepositoryJpaAdapter adapter;

    private ClientEntity sampleEntity() {
        return new ClientEntity(1L, "John", "Doe", "john@example.com", "12345678");
    }

    private Client sampleDomain() {
        return new Client(1L, "John", "Doe", "john@example.com", "12345678");
    }

    @Test
    @DisplayName("save() should convert domain -> entity -> domain")
    void save_shouldPersistAndReturnDomain() {
        Client domain = sampleDomain();
        ClientEntity entity = ClientMapper.toEntity(domain);

        when(jpaRepository.save(entity)).thenReturn(entity);

        Client result = adapter.save(domain);

        assertThat(result).isEqualToComparingFieldByField(domain);
        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("findById() should return mapped domain when found")
    void findById_shouldReturnClient() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(sampleEntity()));

        Optional<Client> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
        verify(jpaRepository).findById(1L);
    }

    @Test
    @DisplayName("findById() should return empty when not found")
    void findById_shouldReturnEmpty() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Client> result = adapter.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmail() should return mapped domain")
    void findByEmail_shouldReturnClient() {
        when(jpaRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleEntity()));

        Optional<Client> result = adapter.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("findByDni() should return mapped domain")
    void findByDni_shouldReturnClient() {
        when(jpaRepository.findByDni("12345678")).thenReturn(Optional.of(sampleEntity()));

        Optional<Client> result = adapter.findByDni("12345678");

        assertThat(result).isPresent();
        assertThat(result.get().getDni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("existsByDni() should delegate to JPA")
    void existsByDni_shouldDelegate() {
        when(jpaRepository.existsByDni("12345678")).thenReturn(true);

        boolean result = adapter.existsByDni("12345678");

        assertThat(result).isTrue();
        verify(jpaRepository).existsByDni("12345678");
    }

    @Test
    @DisplayName("existsByEmail() should delegate to JPA")
    void existsByEmail_shouldDelegate() {
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean result = adapter.existsByEmail("john@example.com");

        assertThat(result).isTrue();
        verify(jpaRepository).existsByEmail("john@example.com");
    }

    @Test
    @DisplayName("findAll() should map all entities to domain")
    void findAll_shouldReturnListOfClients() {
        List<ClientEntity> entities = Arrays.asList(sampleEntity(), new ClientEntity(2L, "Jane", "Doe", "jane@example.com", "87654321"));
        when(jpaRepository.findAll()).thenReturn(entities);

        List<Client> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("deleteById() should delegate to JPA")
    void deleteById_shouldDelegate() {
        adapter.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("existsById() should delegate to JPA")
    void existsById_shouldDelegate() {
        when(jpaRepository.existsById(1L)).thenReturn(true);

        boolean result = adapter.existsById(1L);

        assertThat(result).isTrue();
        verify(jpaRepository).existsById(1L);
    }
}
