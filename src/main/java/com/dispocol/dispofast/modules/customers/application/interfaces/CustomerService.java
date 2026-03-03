package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import org.springframework.data.domain.Page;

public interface CustomerService {

  /**
   * Creates a new customer
   *
   * @param customerRequest The DTO containing customer creation data
   * @return The created CustomerResponseDTO
   */
  CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerRequest);

  /**
   * Retrieves a customer by their ID
   *
   * @param customerId The ID of the customer to retrieve
   * @return The CustomerResponseDTO of the requested customer
   */
  CustomerResponseDTO getCustomerById(String customerId);

  /**
   * Retrieves all customers in a paged format
   *
   * @param page The page number to retrieve
   * @param size The number of customers per page
   * @return A page of CustomerResponseDTOs
   */
  Page<CustomerResponseDTO> getAllCustomers(int page, int size);

  /**
   * Searches customers by a search term in a paged format
   *
   * @param search The search term to filter customers
   * @param page The page number to retrieve
   * @param size The number of customers per page
   * @return A page of CustomerResponseDTOs matching the search term
   */
  Page<CustomerResponseDTO> searchCustomers(String search, int page, int size);

  /**
   * Deletes a customer by their ID
   *
   * @param customerId The ID of the customer to delete
   */
  void deleteCustomerById(String customerId);

  /**
   * Updates an existing customer
   *
   * @param customerId The ID of the customer to update
   * @param customerRequest The DTO containing updated customer data
   * @return The updated CustomerResponseDTO
   */
  CustomerResponseDTO updateCustomer(String customerId, CreateCustomerRequestDTO customerRequest);
}
