package com.dispocol.dispofast.modules.inventory.infra.persistence;

import com.dispocol.dispofast.modules.inventory.domain.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {}
