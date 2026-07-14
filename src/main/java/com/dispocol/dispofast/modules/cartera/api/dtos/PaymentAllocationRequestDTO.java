package com.dispocol.dispofast.modules.cartera.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

/** Monto que un pago combinado aplica a una factura (ArEntry) específica. */
@Data
public class PaymentAllocationRequestDTO {

  @NotNull(message = "La factura es obligatoria")
  private UUID arEntryId;

  @NotNull(message = "El valor es obligatorio")
  @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
  private BigDecimal value;

  /** Porcentaje de descuento por pronto pago elegido para esta factura: 2, 3 o 5. Opcional. */
  private Integer promptPaymentDiscountRate;

  /** Comprobante de este pago específico. Cada factura pagada debe tener el suyo. */
  @NotBlank(message = "El comprobante de pago es obligatorio")
  private String voucherS3Key;
}
