package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.quotes.domain.OfferValidity;
import com.dispocol.dispofast.modules.quotes.domain.PaymentCondition;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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

  /** Tasa del descuento comercial en forma decimal (ej. 0.15 = 15%). Rango válido: [0, 1]. */
  @DecimalMin(value = "0.0", message = "El descuento comercial no puede ser negativo")
  @DecimalMax(value = "1.0", message = "El descuento comercial no puede superar el 100% (1.0)")
  private BigDecimal commercialDiscountRate;

  /** Tasa de otros descuentos en forma decimal (ej. 0.05 = 5%). Rango válido: [0, 1]. */
  @DecimalMin(value = "0.0", message = "Otros descuentos no pueden ser negativos")
  @DecimalMax(value = "1.0", message = "Otros descuentos no pueden superar el 100% (1.0)")
  private BigDecimal otherDiscountsRate;
}
