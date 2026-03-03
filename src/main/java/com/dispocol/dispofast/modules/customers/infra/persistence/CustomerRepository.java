package com.dispocol.dispofast.modules.customers.infra.persistence;

import com.dispocol.dispofast.modules.customers.api.dtos.CustomerResponseDTO;
import com.dispocol.dispofast.modules.customers.domain.Customer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  Customer findByNitCedula(String nitCedula);

  Page<CustomerResponseDTO> findBySocialReasonContainingIgnoreCaseOrNitCedulaContainingIgnoreCase(
      String search, String search2, PageRequest of);
}
