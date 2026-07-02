package com.dispocol.dispofast.modules.shipping.infra.persistence;

import com.dispocol.dispofast.modules.shipping.domain.ShipmentItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, UUID> {

  List<ShipmentItem> findByShipmentId(UUID shipmentId);
}
