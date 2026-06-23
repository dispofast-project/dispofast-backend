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
          + "WHERE year(o.orderDate) = :year "
          + "AND month(o.orderDate) = :month "
          + "AND o.state <> 'CANCELLED'")
  BigDecimal getTotalVentasMes(@Param("year") int year, @Param("month") int month);

  @Query(
      "SELECT year(o.orderDate), month(o.orderDate), COALESCE(SUM(o.totalValue), 0) "
          + "FROM SalesOrder o "
          + "WHERE o.orderDate >= :startDate AND o.state <> 'CANCELLED' "
          + "GROUP BY year(o.orderDate), month(o.orderDate) "
          + "ORDER BY year(o.orderDate), month(o.orderDate)")
  List<Object[]> getMonthlySales(@Param("startDate") OffsetDateTime startDate);

  @Query(
      "SELECT o.asesor.id, o.asesor.fullName, year(o.orderDate), month(o.orderDate), COALESCE(SUM(o.totalValue), 0) "
          + "FROM SalesOrder o "
          + "WHERE o.orderDate >= :startDate AND o.state <> 'CANCELLED' AND o.asesor IS NOT NULL "
          + "GROUP BY o.asesor.id, o.asesor.fullName, year(o.orderDate), month(o.orderDate) "
          + "ORDER BY year(o.orderDate), month(o.orderDate), o.asesor.fullName")
  List<Object[]> getSalesByAsesorAndMonth(@Param("startDate") OffsetDateTime startDate);
}
