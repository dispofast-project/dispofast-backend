package com.dispocol.dispofast.modules.inventory.infra.persistence;

import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {}
