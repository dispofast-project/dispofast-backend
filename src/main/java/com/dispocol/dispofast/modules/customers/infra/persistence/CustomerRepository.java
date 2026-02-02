package com.dispocol.dispofast.modules.customers.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dispocol.dispofast.modules.customers.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    
}
