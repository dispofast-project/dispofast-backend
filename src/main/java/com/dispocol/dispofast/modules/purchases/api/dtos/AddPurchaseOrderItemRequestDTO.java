package com.dispocol.dispofast.modules.purchases.api.dtos;

import jakarta.validation.constraints.NotNull;
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
   * Costo pactado con el proveedor. Siempre requerido — no existe una lista de precios de compra.
   */
  @NotNull(message = "El precio unitario es requerido")
  private BigDecimal unitPrice;
}
