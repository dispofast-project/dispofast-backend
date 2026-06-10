package com.dispocol.dispofast.modules.cartera.infra.persistence;

import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, UUID> {

  List<PaymentReceipt> findByArEntryIdOrderByPaymentDateDesc(UUID arEntryId);

  @Query("SELECT COALESCE(SUM(pr.value), 0) FROM PaymentReceipt pr WHERE pr.state = 'ACTIVE'")
  double sumTotalPaidValue();
}
