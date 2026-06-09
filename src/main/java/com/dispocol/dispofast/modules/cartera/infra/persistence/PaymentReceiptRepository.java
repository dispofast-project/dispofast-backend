package com.dispocol.dispofast.modules.cartera.infra.persistence;

import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, UUID> {

  List<PaymentReceipt> findByArEntryIdOrderByPaymentDateDesc(UUID arEntryId);
}
