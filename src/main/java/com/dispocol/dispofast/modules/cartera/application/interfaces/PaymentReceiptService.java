package com.dispocol.dispofast.modules.cartera.application.interfaces;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import java.util.List;
import java.util.UUID;

public interface PaymentReceiptService {

  PaymentReceiptResponseDTO createReceipt(UUID arEntryId, CreatePaymentReceiptRequestDTO request);

  List<PaymentReceiptResponseDTO> getReceiptsByArEntry(UUID arEntryId);

  PaymentReceiptResponseDTO getReceiptById(UUID id);

  double getTotalPaidValue();
}
