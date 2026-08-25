package com.dispocol.dispofast.modules.purchases.infra.persistence;

import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrderItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {

  List<PurchaseOrderItem> findByPurchaseOrderId(UUID purchaseOrderId);

  Optional<PurchaseOrderItem> findByPurchaseOrder_IdAndProduct_Id(
      UUID purchaseOrderId, UUID productId);
}
