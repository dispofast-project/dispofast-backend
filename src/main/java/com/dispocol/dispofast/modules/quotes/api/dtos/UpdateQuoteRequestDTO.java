package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.quotes.domain.OfferValidity;
import com.dispocol.dispofast.modules.quotes.domain.PaymentCondition;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuoteRequestDTO {
  private QuoteStatus status;
  private UUID sellerId;
  private String locationId;
  private UUID priceListId;
  private PaymentCondition paymentCondition;
  private OfferValidity offerValidity;

  /** Tasa del descuento comercial (ej. 0.15 = 15%). */
  private BigDecimal commercialDiscountRate;

  /** Tasa de otros descuentos adicionales (ej. 0.05 = 5%). */
  private BigDecimal otherDiscountsRate;
}
