package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommissionRateDTO {

  @NotNull(message = "La categoría es obligatoria")
  private UUID categoryId;

  @NotNull(message = "El porcentaje de comisión es obligatorio")
  @DecimalMin(value = "0.0", inclusive = true, message = "La comisión debe ser mayor o igual a 0")
  private BigDecimal rate;
}
