package com.dispocol.dispofast.modules.customers.api.mappers;

import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import com.dispocol.dispofast.modules.customers.domain.Customer;

public interface CustomerMapper {
    
    
    CustomerResponseDTO toCustomerResponseDTO(Customer customer);
}
