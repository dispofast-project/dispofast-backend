package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserProductAllocationDTO {

  @NotNull(message = "El producto es obligatorio")
  private UUID productId;

  @NotNull(message = "El cupo asignado es obligatorio")
  @Min(value = 0, message = "El cupo asignado no puede ser negativo")
  private Integer assignedQuantity;
}
