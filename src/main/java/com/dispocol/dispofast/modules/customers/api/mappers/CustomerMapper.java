package com.dispocol.dispofast.modules.customers.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerContactRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateCustomerRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import com.dispocol.dispofast.modules.customers.domain.Customer;
import com.dispocol.dispofast.modules.customers.domain.CustomerContact;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    @Mapping(target = "socialReason", source = "socialReason")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "nitCedula", source = "nitCedula")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "assor", expression = "java(customer.getUser() != null ? customer.getUser().getFullName() : null)")
    CustomerResponseDTO toCustomerResponseDTO(Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "emailFactElec", ignore = true)
    @Mapping(target = "classification", ignore = true)
    @Mapping(target = "typeClient", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "legalDocs", ignore = true)
    @Mapping(target = "creditProfile", ignore = true)
    @Mapping(
        target = "socialReason", 
        expression = "java(createCustomerRequestDTO != null ? createCustomerRequestDTO.getSocialReason().trim() : null)"
    )
    @Mapping(
        target = "nitCedula", 
        expression = "java(createCustomerRequestDTO != null ? createCustomerRequestDTO.getNitCedula().trim() : null)"
    )
    @Mapping(
        target = "email",
        expression = "java(createCustomerRequestDTO != null ? createCustomerRequestDTO.getEmail().trim().toLowerCase() : null)"
    )
    @Mapping(
        target = "phone",
        expression = "java(createCustomerRequestDTO != null ? createCustomerRequestDTO.getPhone().trim() : null)"
    )
    @Mapping(
        target = "typePerson",
        expression = "java(createCustomerRequestDTO != null ? createCustomerRequestDTO.getTypePerson().trim() : null)"
    )
    Customer fromCreateCustomerRequestDTO(CreateCustomerRequestDTO createCustomerRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(
        target = "contactName",
        expression = "java(createCustomerContactRequestDTO != null ? createCustomerContactRequestDTO.getName().trim() : null)"
    )
    @Mapping(
        target = "email",
        expression = "java(createCustomerContactRequestDTO != null ? createCustomerContactRequestDTO.getEmail().trim().toLowerCase() : null)"
    )
    @Mapping(
        target = "phone",
        expression = "java(createCustomerContactRequestDTO != null ? createCustomerContactRequestDTO.getPhone().trim() : null)"
    )
    @Mapping(
        target = "contactLastName",
        expression = "java(createCustomerContactRequestDTO != null ? createCustomerContactRequestDTO.getLastName().trim() : null)"
    )
    @Mapping(
        target = "position",
        expression = "java(createCustomerContactRequestDTO != null ? createCustomerContactRequestDTO.getPosition().trim() : null)"
    )
    CustomerContact fromCreateCustomerContactRequestDTO(CreateCustomerContactRequestDTO createCustomerContactRequestDTO);
}
