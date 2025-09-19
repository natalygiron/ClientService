package com.bootcamp.clientservice.dto.external;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private double balance;
    private AccountType type;
    private Long clientId;
}
