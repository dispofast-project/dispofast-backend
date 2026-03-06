package com.dispocol.dispofast.modules.orders.infra.persistence;

import com.dispocol.dispofast.modules.orders.domain.SalesOrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, UUID> {

  List<SalesOrderItem> findByOrderId(UUID orderId);

  void deleteByOrderId(UUID orderId);
}
