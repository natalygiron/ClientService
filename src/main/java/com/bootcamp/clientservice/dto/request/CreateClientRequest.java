package com.bootcamp.clientservice.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClientRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^[0-9]+$", message = "DNI must contain only digits")
    @NotBlank(message = "DNI is required")
    private String dni;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
