package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;

public interface CustomerService {
    
    /**
     * Creates a new customer
     * @param customerRequest The DTO containing customer creation data
     * @return The created CustomerResponseDTO
     */
    CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerRequest);

    /**
     * Retrieves a customer by their ID
     * @param customerId The ID of the customer to retrieve
     * @return The CustomerResponseDTO of the requested customer
     */
    CustomerResponseDTO getCustomerById(String customerId);

    /**
     * Deletes a customer by their ID
     * @param customerId The ID of the customer to delete
     */
    void deleteCustomerById(String customerId);


}
