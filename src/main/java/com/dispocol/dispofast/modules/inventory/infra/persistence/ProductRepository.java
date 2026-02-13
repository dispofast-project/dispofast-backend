package com.dispocol.dispofast.modules.inventory.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dispocol.dispofast.modules.inventory.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    
}
