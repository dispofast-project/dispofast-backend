package com.dispocol.dispofast.modules.invoices.infra.persistence;

import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

  Optional<Invoice> findBySalesOrderId(UUID salesOrderId);
}
