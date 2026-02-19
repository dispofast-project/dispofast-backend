package com.dispocol.dispofast.modules.inventory.infra.persistence;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  Product findBySeoTitle(String seoTitle);

  boolean existsBySeoTitle(String seoTitle);
}
