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
  private BigDecimal unitPrice;
  private BigDecimal discountAmount;
}
