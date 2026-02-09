package com.dispocol.dispofast.modules.customers.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponseDTO {
    
    private String socialReason;
    private String nitCedula;
    private String email;
    private String phone;
    private String assor;
    
}
