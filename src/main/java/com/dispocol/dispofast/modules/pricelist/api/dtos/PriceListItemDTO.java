package com.dispocol.dispofast.modules.pricelist.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListItemDTO {
  private UUID productId;
  private String productReference;
  private BigDecimal unitPrice;
}
