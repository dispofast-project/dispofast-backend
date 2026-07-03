package com.dispocol.dispofast.modules.cartera.infra.persistence;

import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArEntryRepository
    extends JpaRepository<ArEntry, UUID>, JpaSpecificationExecutor<ArEntry> {

  @Query(
      "SELECT COALESCE(SUM(a.value - a.paidAmount), 0) FROM ArEntry a "
          + "WHERE a.state = 'PENDING' "
          + "AND (:vendedorEmail IS NULL OR a.client.defaultAdvisor.email = :vendedorEmail)")
  BigDecimal getTotalCartera(@Param("vendedorEmail") String vendedorEmail);

  @Query(
      "SELECT COALESCE(SUM(a.value - a.paidAmount), 0) FROM ArEntry a "
          + "WHERE a.state = 'PENDING' AND a.expirationDate < :now "
          + "AND (:vendedorEmail IS NULL OR a.client.defaultAdvisor.email = :vendedorEmail)")
  BigDecimal getCarteraVencida(
      @Param("now") OffsetDateTime now, @Param("vendedorEmail") String vendedorEmail);

  @Query(
      "SELECT DISTINCT a.fullName FROM ArEntry e JOIN e.asesor a "
          + "WHERE (:vendedorEmail IS NULL OR e.client.defaultAdvisor.email = :vendedorEmail) "
          + "ORDER BY a.fullName")
  List<String> findDistinctAsesorNames(@Param("vendedorEmail") String vendedorEmail);
}
