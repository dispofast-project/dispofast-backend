package com.dispocol.dispofast.modules.customers.api.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCustomerContactRequestDTO {
    
    private String name;
    private String lastName;
    private String position;
    private String phone;

    @Email
    private String email;

}
