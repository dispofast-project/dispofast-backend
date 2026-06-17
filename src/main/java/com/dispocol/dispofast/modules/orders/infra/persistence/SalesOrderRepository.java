package com.dispocol.dispofast.modules.orders.infra.persistence;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderRepository
    extends JpaRepository<SalesOrder, UUID>, JpaSpecificationExecutor<SalesOrder> {

  boolean existsByOrderNumber(String orderNumber);

  boolean existsByQuoteId(UUID quoteId);

  boolean existsByState(OrderState state);

  @Query(
      "SELECT COALESCE(SUM(o.totalValue), 0) FROM SalesOrder o "
          + "WHERE FUNCTION('YEAR', o.orderDate) = :year "
          + "AND FUNCTION('MONTH', o.orderDate) = :month "
          + "AND o.state <> 'CANCELLED'")
  BigDecimal getTotalVentasMes(@Param("year") int year, @Param("month") int month);

  @Query(
      "SELECT FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate), "
          + "COALESCE(SUM(o.totalValue), 0) "
          + "FROM SalesOrder o "
          + "WHERE o.orderDate >= :startDate AND o.state <> 'CANCELLED' "
          + "GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate) "
          + "ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
  List<Object[]> getMonthlySales(@Param("startDate") OffsetDateTime startDate);
}
