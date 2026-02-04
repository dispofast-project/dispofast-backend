package com.dispocol.dispofast.modules.customers.application.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerContactRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import com.dispocol.dispofast.modules.customers.api.mappers.CustomerMapper;
import com.dispocol.dispofast.modules.customers.application.interfaces.CustomerService;
import com.dispocol.dispofast.modules.customers.domain.Customer;
import com.dispocol.dispofast.modules.customers.domain.CustomerContact;
import com.dispocol.dispofast.modules.customers.infra.persistence.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerRequest) {
        Customer newCustomer;
        String nitCedula;

        newCustomer = new Customer();
        nitCedula = customerRequest.getNitCedula();

        if (customerRepository.findByNitCedula(nitCedula) != null) {
            throw new IllegalStateException("El cliente ya existe con el NIT/Cédula: " 
                + nitCedula
            );
        }

        CustomerContact contact = createCustomerContact(customerRequest.getContact());

        newCustomer = customerMapper.fromCreateCustomerRequestDTO(customerRequest);
        List<CustomerContact> contacts = newCustomer.getContacts();
        contacts.add(contact);
        newCustomer.setContacts(contacts);

        newCustomer = customerRepository.save(newCustomer);

        return customerMapper.toCustomerResponseDTO(newCustomer);

    }

    private CustomerContact createCustomerContact(CreateCustomerContactRequestDTO customerRequest) {

    
        return customerMapper.fromCreateCustomerContactRequestDTO(customerRequest);
    }

    @Override
    public CustomerResponseDTO getCustomerById(String customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCustomerById'");
    }

    @Override
    public Page<CustomerResponseDTO> getAllCustomers(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size))
            .map(customerMapper::toCustomerResponseDTO);
    }

    @Override
    public Page<CustomerResponseDTO> searchCustomers(String search, int page, int size) {
        return customerRepository.findBySocialReasonContainingIgnoreCaseOrNitCedulaContainingIgnoreCase(
            search, 
            search, 
            PageRequest.of(page, size)
        );
    }

    @Override
    public void deleteCustomerById(String customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCustomerById'");
    }

    @Override
    public CustomerResponseDTO updateCustomer(String customerId, CreateCustomerRequestDTO customerRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCustomer'");
    }
    
}
