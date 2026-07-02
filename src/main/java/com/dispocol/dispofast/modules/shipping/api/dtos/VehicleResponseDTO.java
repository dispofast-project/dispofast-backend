package com.dispocol.dispofast.modules.shipping.api.dtos;

import com.dispocol.dispofast.modules.shipping.domain.VehicleState;
import com.dispocol.dispofast.modules.shipping.domain.VehicleType;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDTO {

  private UUID id;
  private String plate;
  private VehicleState state;
  private VehicleType type;
  private LocalDate createdAt;
}
