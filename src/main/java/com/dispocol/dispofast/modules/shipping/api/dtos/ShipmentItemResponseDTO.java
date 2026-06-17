package com.dispocol.dispofast.modules.shipping.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItemResponseDTO {
  private UUID id;
  private String productSku;
  private String productName;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private Integer taxPercent;
  private BigDecimal total;
}
