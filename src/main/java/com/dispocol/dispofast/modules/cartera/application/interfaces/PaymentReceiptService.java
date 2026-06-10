package com.dispocol.dispofast.modules.cartera.application.interfaces;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentReceiptService {

  PaymentReceiptResponseDTO createReceipt(UUID arEntryId, CreatePaymentReceiptRequestDTO request);

  List<PaymentReceiptResponseDTO> getReceiptsByArEntry(UUID arEntryId);

  PaymentReceiptResponseDTO getReceiptById(UUID id);

  double getTotalPaidValue();

  String uploadVoucher(MultipartFile file);

  byte[] downloadVoucher(UUID receiptId);

  String getVoucherFilename(UUID receiptId);
}
