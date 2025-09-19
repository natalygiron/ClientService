package com.bootcamp.clientservice.dto.request;

import javax.validation.constraints.Email;
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
public class PatchClientRequest {

    private String firstName;

    private String lastName;

    private String dni;

    @Email(message = "Email must be valid")
    @Pattern(regexp = ".+@.+\\..+", message = "Email must be valid")
    private String email;
}

