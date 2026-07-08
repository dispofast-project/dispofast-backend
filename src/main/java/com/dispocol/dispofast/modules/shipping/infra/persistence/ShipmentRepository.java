package com.dispocol.dispofast.modules.shipping.infra.persistence;

import com.dispocol.dispofast.modules.shipping.domain.Shipment;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

  Optional<Shipment> findByInvoiceId(UUID invoiceId);

  List<Shipment> findByInvoiceIdIn(List<UUID> invoiceIds);

  List<Shipment> findByCarrierId(UUID carrierId);

  @Query("SELECT s.state, COUNT(s) FROM Shipment s GROUP BY s.state")
  List<Object[]> countGroupedByState();

  @Query(
      value =
          """
          SELECT * FROM shipments s WHERE
            (CAST(:state AS text) IS NULL OR s.state = :state) AND
            (CAST(:clientName AS text) IS NULL OR LOWER(s.client_name) LIKE LOWER('%' || :clientName || '%')) AND
            (CAST(:asesorName AS text) IS NULL OR LOWER(s.asesor_name) LIKE LOWER('%' || :asesorName || '%')) AND
            (CAST(:dateFrom AS timestamptz) IS NULL OR s.created_at >= CAST(:dateFrom AS timestamptz)) AND
            (CAST(:dateTo AS timestamptz)   IS NULL OR s.created_at <= CAST(:dateTo AS timestamptz))
          ORDER BY s.created_at DESC
          """,
      countQuery =
          """
          SELECT COUNT(*) FROM shipments s WHERE
            (CAST(:state AS text) IS NULL OR s.state = :state) AND
            (CAST(:clientName AS text) IS NULL OR LOWER(s.client_name) LIKE LOWER('%' || :clientName || '%')) AND
            (CAST(:asesorName AS text) IS NULL OR LOWER(s.asesor_name) LIKE LOWER('%' || :asesorName || '%')) AND
            (CAST(:dateFrom AS timestamptz) IS NULL OR s.created_at >= CAST(:dateFrom AS timestamptz)) AND
            (CAST(:dateTo AS timestamptz)   IS NULL OR s.created_at <= CAST(:dateTo AS timestamptz))
          """,
      nativeQuery = true)
  Page<Shipment> findWithFilters(
      @Param("state") String state,
      @Param("clientName") String clientName,
      @Param("asesorName") String asesorName,
      @Param("dateFrom") OffsetDateTime dateFrom,
      @Param("dateTo") OffsetDateTime dateTo,
      Pageable pageable);
}
