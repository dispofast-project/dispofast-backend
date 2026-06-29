package com.dispocol.dispofast.modules.cartera.infra.persistence;

import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArEntryRepository
    extends JpaRepository<ArEntry, UUID>, JpaSpecificationExecutor<ArEntry> {

  @Query(
      "SELECT COALESCE(SUM(a.value - a.paidAmount), 0) FROM ArEntry a "
          + "WHERE a.state = 'PENDING' AND a.expirationDate < :now")
  BigDecimal getCarteraVencida(@Param("now") OffsetDateTime now);
}
