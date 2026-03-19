package com.dispocol.dispofast.modules.cartera.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateManualArEntryRequestDTO {

  @NotNull(message = "El cliente es obligatorio")
  private UUID clientId;

  private UUID asesorUserId;

  @NotNull(message = "El valor es obligatorio")
  @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
  private BigDecimal value;

  @NotBlank(message = "El número de factura es obligatorio")
  private String invoiceNumber;

  @NotNull(message = "La fecha de factura es obligatoria")
  private OffsetDateTime invoiceDate;

  @Min(value = 1, message = "El plazo debe ser al menos 1 día")
  private int paymentTermDays = 30;

  /** Código de ciudad (opcional) */
  private String cityId;
}
