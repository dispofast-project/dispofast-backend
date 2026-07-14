package com.dispocol.dispofast.modules.cartera.application.interfaces;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreateMultiInvoicePaymentRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.MultiInvoicePaymentResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentReceiptService {

  PaymentReceiptResponseDTO createReceipt(UUID arEntryId, CreatePaymentReceiptRequestDTO request);

  /**
   * Registra un solo pago del cliente repartido entre varias de sus facturas pendientes, de forma
   * atómica: o se aplican todas las asignaciones o ninguna.
   */
  MultiInvoicePaymentResponseDTO createMultiInvoicePayment(
      CreateMultiInvoicePaymentRequestDTO request);

  List<PaymentReceiptResponseDTO> getReceiptsByArEntry(UUID arEntryId);

  PaymentReceiptResponseDTO getReceiptById(UUID id);

  double getTotalPaidValue();

  String uploadVoucher(MultipartFile file);

  byte[] downloadVoucher(UUID receiptId);

  String getVoucherFilename(UUID receiptId);
}
