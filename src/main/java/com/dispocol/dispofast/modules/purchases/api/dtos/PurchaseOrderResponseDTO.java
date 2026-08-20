package com.dispocol.dispofast.modules.purchases.api.dtos;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.domain.RetefuenteType;
import com.dispocol.dispofast.modules.purchases.domain.PaymentCondition;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponseDTO {

  private UUID id;
  private String number;
  private PaymentCondition paymentCondition;
  private ClientResponseDTO supplier;
  private UUID buyerId;
  private String buyerName;
  private List<PurchaseOrderItemResponseDTO> items;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  // ── Detalles de pago ─────────────────────────────────────────
  private BigDecimal subtotalAmount;
  private BigDecimal commercialDiscountRate;
  private BigDecimal commercialDiscountAmount;
  private BigDecimal otherDiscountsRate;
  private BigDecimal otherDiscountsAmount;
  private BigDecimal ivaRate;
  private BigDecimal ivaAmount;
  private BigDecimal retefuenteRate;
  private BigDecimal retefuenteAmount;
  private RetefuenteType retefuenteTypeOverride;
  private BigDecimal totalAmount;
  private BigDecimal freight;
}
