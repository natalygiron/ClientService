package com.bootcamp.clientservice.infrastructure.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("email: Email must be valid")
                .path("/clientes")
                .build();

        assertNotNull(response);
        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("email: Email must be valid", response.getMessage());
        assertEquals("/clientes", response.getPath());
    }

    @Test
    void testSettersAndEqualsHashCode() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse response1 = new ErrorResponse(now, 400, "Bad Request", "Invalid data", "/clientes");
        ErrorResponse response2 = new ErrorResponse(now, 400, "Bad Request", "Invalid data", "/clientes");

        // Lombok @Data genera equals y hashCode
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());

        // Probar setters
        response1.setMessage("Updated message");
        assertEquals("Updated message", response1.getMessage());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("Client not found")
                .path("/clientes/1")
                .build();

        String toString = response.toString();

        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
        assertTrue(toString.contains("Client not found"));
        assertTrue(toString.contains("/clientes/1"));
    }
}
