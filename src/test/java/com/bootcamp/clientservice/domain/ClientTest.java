package com.bootcamp.clientservice.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.bootcamp.clientservice.domain.model.Client;

class ClientTest {

    /**
     * Verifica que el constructor con ID inicializa correctamente todos los campos.
     */
    @Test
    void shouldCreateClientWithId() {
        Client client = new Client(1L, "John", "Doe", "john.doe@example.com", "12345678");

        assertEquals(1L, client.getId());
        assertEquals("John", client.getFirstName());
        assertEquals("Doe", client.getLastName());
        assertEquals("john.doe@example.com", client.getEmail());
        assertEquals("12345678", client.getDni());
    }

    /**
     * Verifica que el constructor sin ID asigne null al campo id.
     */
    @Test
    void shouldCreateClientWithoutId() {
        Client client = new Client("Jane", "Smith", "jane.smith@example.com", "87654321");

        assertNull(client.getId());
        assertEquals("Jane", client.getFirstName());
        assertEquals("Smith", client.getLastName());
        assertEquals("jane.smith@example.com", client.getEmail());
        assertEquals("87654321", client.getDni());
    }

    /**
     * Verifica que se lance una excepción si algún campo requerido es null.
     */
    @Test
    void shouldThrowExceptionWhenRequiredFieldIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Client(1L, null, "Doe", "john.doe@example.com", "12345678"),
                "Debe fallar si firstName es null");

        assertThrows(NullPointerException.class,
                () -> new Client(1L, "John", null, "john.doe@example.com", "12345678"),
                "Debe fallar si lastName es null");

        assertThrows(NullPointerException.class,
                () -> new Client(1L, "John", "Doe", null, "12345678"),
                "Debe fallar si email es null");

        assertThrows(NullPointerException.class,
                () -> new Client(1L, "John", "Doe", "john.doe@example.com", null),
                "Debe fallar si dni es null");
    }

    /**
     * Verifica que dos clientes con el mismo email y dni son considerados iguales,
     * sin importar el ID o el nombre.
     */
    @Test
    void shouldConsiderClientsEqualWhenEmailAndDniAreSame() {
        Client client1 = new Client(1L, "John", "Doe", "same@example.com", "11111111");
        Client client2 = new Client(2L, "Jane", "Smith", "same@example.com", "11111111");

        assertEquals(client1, client2);
        assertEquals(client1.hashCode(), client2.hashCode(), "hashCode debe ser igual para email y dni iguales");
    }

    /**
     * Verifica que dos clientes con diferente email o dni no son iguales.
     */
    @Test
    void shouldConsiderClientsNotEqualWhenEmailOrDniDifferent() {
        Client client1 = new Client(1L, "John", "Doe", "john@example.com", "11111111");
        Client client2 = new Client(2L, "Jane", "Smith", "jane@example.com", "22222222");

        assertNotEquals(client1, client2);
    }

    /**
     * Verifica que un cliente nunca es igual a null o a otro objeto de otra clase.
     */
    @Test
    void shouldNotBeEqualToNullOrDifferentClass() {
        Client client = new Client(1L, "John", "Doe", "john@example.com", "11111111");

        assertNotEquals(client, null);
        assertNotEquals(client, "Some String");
    }
}
