package com.dispocol.dispofast.modules.quotes.api.dtos;

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
public class AddQuoteItemRequestDTO {

  private UUID productId;
  private BigDecimal quantity;

  /** Si se envía, sobreescribe el precio de la lista de precios. */
  private BigDecimal unitPrice;
}
