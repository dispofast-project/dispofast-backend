package com.dispocol.dispofast.modules.orders.infra.persistence;

import com.dispocol.dispofast.modules.orders.domain.SalesOrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, UUID> {

  List<SalesOrderItem> findByOrderId(UUID orderId);

  /** Fetches items (with their product) for several orders in one query, for list views. */
  @Query("SELECT i FROM SalesOrderItem i JOIN FETCH i.product WHERE i.order.id IN :orderIds")
  List<SalesOrderItem> findByOrderIdInWithProduct(@Param("orderIds") List<UUID> orderIds);

  void deleteByOrderId(UUID orderId);

  @Query(
      "SELECT i.product.name, SUM(i.quantity) FROM SalesOrderItem i "
          + "GROUP BY i.product.id, i.product.name "
          + "ORDER BY SUM(i.quantity) DESC")
  List<Object[]> getTopProducts(Pageable pageable);

  @Query(
      "SELECT i FROM SalesOrderItem i JOIN FETCH i.order o "
          + "WHERE o.client.id = :clientId AND i.product.id = :productId "
          + "ORDER BY o.orderDate DESC")
  List<SalesOrderItem> findByClientIdAndProductId(
      @Param("clientId") UUID clientId, @Param("productId") UUID productId);
}
