package com.bootcamp.clientservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(
        name = "clients",
        indexes = {
                @Index( name = "idx_clients_email_unique",
                        columnList = "email",
                        unique = true),
                @Index( name = "idx_clients_dni_unique",
                        columnList = "dni",
                        unique = true)
        }
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Size(min = 8, max = 12)
    @Column(nullable = false, unique = true)
    private String dni;
}
