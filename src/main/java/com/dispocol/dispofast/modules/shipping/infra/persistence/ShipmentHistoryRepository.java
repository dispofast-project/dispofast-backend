package com.dispocol.dispofast.modules.shipping.infra.persistence;

import com.dispocol.dispofast.modules.shipping.domain.ShipmentHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentHistoryRepository extends JpaRepository<ShipmentHistory, UUID> {

  List<ShipmentHistory> findByShipmentIdOrderByChangedAtDesc(UUID shipmentId);
}
