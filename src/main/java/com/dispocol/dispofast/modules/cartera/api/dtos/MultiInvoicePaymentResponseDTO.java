package com.dispocol.dispofast.modules.cartera.api.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class MultiInvoicePaymentResponseDTO {

  private UUID paymentGroupId;
  private List<PaymentReceiptResponseDTO> receipts;

  /** Suma de lo aplicado a las facturas (efectivo + descuentos por pronto pago). */
  private BigDecimal totalApplied;
}
