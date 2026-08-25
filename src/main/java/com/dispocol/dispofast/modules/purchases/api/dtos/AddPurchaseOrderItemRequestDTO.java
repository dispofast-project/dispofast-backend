package com.dispocol.dispofast.modules.purchases.api.dtos;

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
public class AddPurchaseOrderItemRequestDTO {

  private UUID productId;
  private BigDecimal quantity;

  /**
   * Costo pactado con el proveedor. Opcional — si no se envía, el ítem queda en 0 y se puede
   * completar después.
   */
  private BigDecimal unitPrice;
}
