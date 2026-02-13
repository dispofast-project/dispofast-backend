package com.dispocol.dispofast.modules.inventory.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {
    
}
