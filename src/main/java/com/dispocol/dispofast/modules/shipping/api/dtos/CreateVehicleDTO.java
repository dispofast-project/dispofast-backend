package com.dispocol.dispofast.modules.shipping.api.dtos;

import com.dispocol.dispofast.modules.shipping.domain.VehicleState;
import com.dispocol.dispofast.modules.shipping.domain.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleDTO {

  private String plate;
  private VehicleState state;
  private VehicleType type;
}
