package com.dispocol.dispofast.modules.cartera.api.dtos;

import com.dispocol.dispofast.modules.cartera.domain.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreatePaymentReceiptRequestDTO {

  @NotNull(message = "El valor es obligatorio")
  @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
  private BigDecimal value;

  @NotNull(message = "La fecha de pago es obligatoria")
  private LocalDate paymentDate;

  @NotNull(message = "El método de pago es obligatorio")
  private PaymentMethod paymentMethod;

  private String documentNumber;

  private String voucherS3Key;

  private String observations;
}
