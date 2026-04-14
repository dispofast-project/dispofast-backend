package com.dispocol.dispofast.modules.orders.infra.persistence;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderRepository
    extends JpaRepository<SalesOrder, UUID>, JpaSpecificationExecutor<SalesOrder> {

  boolean existsByOrderNumber(String orderNumber);

  boolean existsByQuoteId(UUID quoteId);

  boolean existsByState(OrderState state);
}
