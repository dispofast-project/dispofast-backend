package com.dispocol.dispofast.modules.inventory.infra.persistence;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  @Query(
      value = "SELECT p FROM Product p JOIN FETCH p.category",
      countQuery = "SELECT count(p) FROM Product p")
  Page<Product> findAllWithCategory(Pageable pageable);

  Product findBySeoTitle(String seoTitle);

  boolean existsBySeoTitle(String seoTitle);

  boolean existsByName(String name);

  java.util.Optional<Product> findByReference(String reference);

  java.util.List<Product> findAllBySku(String sku);
}
