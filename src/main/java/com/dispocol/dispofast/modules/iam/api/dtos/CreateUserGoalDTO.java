package com.dispocol.dispofast.modules.iam.api.dtos;

import com.dispocol.dispofast.modules.iam.domain.GoalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserGoalDTO {

  @NotNull(message = "El tipo de meta es obligatorio")
  private GoalType type;

  @NotNull(message = "El mes es obligatorio")
  @Min(value = 1, message = "El mes debe estar entre 1 y 12")
  @Max(value = 12, message = "El mes debe estar entre 1 y 12")
  private Integer month;

  @NotNull(message = "El año es obligatorio")
  @Min(value = 2000, message = "El año no es válido")
  private Integer year;

  @NotNull(message = "El valor de la meta es obligatorio")
  @DecimalMin(value = "0.0", inclusive = true, message = "El valor debe ser mayor o igual a 0")
  private BigDecimal value;
}
