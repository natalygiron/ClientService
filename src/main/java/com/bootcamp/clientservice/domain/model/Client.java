package com.bootcamp.clientservice.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio pura (sin dependencias de frameworks).
 */
public class Client {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dni;

    public Client(Long id, String firstName, String lastName, String email, String dni) {
        this.id = id;
        this.firstName = Objects.requireNonNull(firstName, "firstName is required");
        this.lastName = Objects.requireNonNull(lastName, "lastName is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.dni = Objects.requireNonNull(dni, "dni is required");
    }

    // Constructor sin ID
    public Client(String firstName, String lastName, String email, String dni) {
        this(null, firstName, lastName, email, dni);
    }

    // Getters (sin setters para inmutabilidad)
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getDni() { return dni; }

    // Igualdad basada en email y dni
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return email.equals(client.email) && dni.equals(client.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, dni);
    }
}
