package com.dispocol.dispofast.modules.orders.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSalesOrderItemDTO {

  private UUID productId;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private BigDecimal discount;
  private BigDecimal lineTotal;
}
