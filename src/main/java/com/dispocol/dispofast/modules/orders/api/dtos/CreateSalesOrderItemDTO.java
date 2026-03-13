package com.dispocol.dispofast.modules.orders.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSalesOrderItemDTO {

  @NotNull(message = "El producto es obligatorio")
  private UUID productId;

  @NotNull(message = "La cantidad es obligatoria")
  @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
  private BigDecimal quantity;

  @NotNull(message = "El precio unitario es obligatorio")
  @DecimalMin(
      value = "0.00",
      inclusive = true,
      message = "El precio unitario no puede ser negativo")
  private BigDecimal unitPrice;

  @DecimalMin(value = "0.00", inclusive = true, message = "El descuento no puede ser negativo")
  private BigDecimal discount;

  @NotNull(message = "El total de línea es obligatorio")
  @DecimalMin(value = "0.00", inclusive = true, message = "El total de línea no puede ser negativo")
  private BigDecimal lineTotal;
}
