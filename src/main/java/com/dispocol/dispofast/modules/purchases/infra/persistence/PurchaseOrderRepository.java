package com.dispocol.dispofast.modules.purchases.infra.persistence;

import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrder;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

  /**
   * Busca órdenes de compra por texto, con coincidencia parcial e insensible a mayúsculas. El
   * parámetro {@code key} determina el campo:
   *
   * <ul>
   *   <li>{@code "buyer"} — nombre completo del comprador
   *   <li>{@code "number"} — número de la orden
   *   <li>{@code "supplier"} — número de identificación del proveedor
   *   <li>Cualquier otro valor / null — busca en los tres campos
   * </ul>
   */
  @EntityGraph(attributePaths = {"buyer", "supplier"})
  @Query(
      """
      SELECT po FROM PurchaseOrder po
      WHERE
        CASE :key
          WHEN 'buyer' THEN LOWER(po.buyer.fullName)
          WHEN 'number' THEN LOWER(po.number)
          WHEN 'supplier' THEN LOWER(po.supplier.identificationNumber)
          ELSE ''
        END LIKE LOWER(CONCAT('%', :text, '%'))
        OR (:key NOT IN ('buyer', 'number', 'supplier') AND (
              LOWER(po.buyer.fullName)                 LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(po.number)                        LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(po.supplier.identificationNumber) LIKE LOWER(CONCAT('%', :text, '%'))
        ))
      """)
  Page<PurchaseOrder> searchByText(
      @Param("text") String text, @Param("key") String key, Pageable pageable);

  @EntityGraph(attributePaths = {"buyer", "supplier"})
  @Query("SELECT po FROM PurchaseOrder po")
  Page<PurchaseOrder> findAllWithRelations(Pageable pageable);
}
