package com.dispocol.dispofast.modules.customers.api.dtos;

import com.dispocol.dispofast.modules.iam.domain.AppUser;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateCustomerRequestDTO {
    
    private String typePerson;
    private String socialReason;
    private String nitCedula;
    private boolean witholdingTax;

    private String address;
    private String zone;
    private String city;
    private String country;
    private String depto;
    private String phone;

    @Email
    private String email;

    private AppUser appUser;

    private CreateCustomerContactRequestDTO contact;
}
