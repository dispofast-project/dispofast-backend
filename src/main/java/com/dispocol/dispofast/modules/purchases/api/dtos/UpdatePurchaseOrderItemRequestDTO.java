package com.dispocol.dispofast.modules.purchases.api.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePurchaseOrderItemRequestDTO {

  private BigDecimal quantity;
  private BigDecimal unitPrice;
}
