package com.dispocol.dispofast.modules.orders.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class SalesOrderItemResponseDTO {

  private UUID id;
  private UUID productId;
  private String productName;
  private String productReference;
  private boolean taxFree;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private BigDecimal discount;
  private BigDecimal lineTotal;
}
