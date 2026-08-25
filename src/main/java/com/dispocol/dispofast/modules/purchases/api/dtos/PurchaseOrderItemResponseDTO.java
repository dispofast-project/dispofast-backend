package com.dispocol.dispofast.modules.purchases.api.dtos;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
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
public class PurchaseOrderItemResponseDTO {

  private UUID id;
  private ProductResponseDTO product;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private BigDecimal taxRate;
  private BigDecimal taxAmount;
  private BigDecimal lineTotal;
}
