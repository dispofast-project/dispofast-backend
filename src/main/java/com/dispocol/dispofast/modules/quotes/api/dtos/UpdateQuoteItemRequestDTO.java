package com.dispocol.dispofast.modules.quotes.api.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuoteItemRequestDTO {

  private BigDecimal quantity;
  private BigDecimal unitPrice;
}
