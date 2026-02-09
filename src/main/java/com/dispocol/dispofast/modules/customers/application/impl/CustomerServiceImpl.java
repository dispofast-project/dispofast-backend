package com.dispocol.dispofast.modules.customers.application.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;

    @Override
    public CustomerResponseDTO createCustomer(CreateCustomerRequestDTO customerRequest) {
        
        String nitCedula = customerRequest.getNitCedula();

        if (customerRepository.findByNitCedula(nitCedula) != null) {
            throw new IllegalStateException("El cliente ya existe con el NIT/Cédula: " + nitCedula);
        }

        if (customerRequest.getUserId() == null) {
            throw new IllegalStateException("El userId es obligatorio");
        }

        AppUser appUser = userRepository.findById(customerRequest.getUserId())
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con ID: " + customerRequest.getUserId()));

        Customer newCustomer = customerMapper.fromCreateCustomerRequestDTO(customerRequest);
        newCustomer.setUser(appUser);

        CustomerContact contact = createCustomerContact(customerRequest.getContact());
        List<CustomerContact> contacts = new ArrayList<>();
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
        return customerRepository.findById(UUID.fromString(customerId))
            .map(customerMapper::toCustomerResponseDTO)
            .orElseThrow(() -> new IllegalStateException("Cliente no encontrado con ID: " + customerId));
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
       
        Customer customer = customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new IllegalStateException("Cliente no encontrado con ID: " + customerId));
        
        customerRepository.delete(customer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(String customerId, CreateCustomerRequestDTO customerRequest) {
        
        Customer existingCustomer = customerRepository.findById(UUID.fromString(customerId))
            .orElseThrow(() -> new IllegalStateException("Cliente no encontrado con ID: " + customerId));

        existingCustomer.setTypePerson(customerRequest.getTypePerson().trim());
        existingCustomer.setSocialReason(customerRequest.getSocialReason().trim());
        existingCustomer.setNitCedula(customerRequest.getNitCedula().trim());
        existingCustomer.setWitholdingTax(customerRequest.isWitholdingTax());
        existingCustomer.setAddress(customerRequest.getAddress().trim());
        existingCustomer.setZone(customerRequest.getZone().trim());
        existingCustomer.setCity(customerRequest.getCity().trim());
        existingCustomer.setCountry(customerRequest.getCountry().trim());
        existingCustomer.setDepto(customerRequest.getDepto().trim());
        existingCustomer.setPhone(customerRequest.getPhone().trim());
        existingCustomer.setEmail(customerRequest.getEmail().trim().toLowerCase());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toCustomerResponseDTO(updatedCustomer);
    }
    
}
