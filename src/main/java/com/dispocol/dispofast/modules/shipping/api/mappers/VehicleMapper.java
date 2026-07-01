package com.dispocol.dispofast.modules.shipping.api.mappers;

import com.dispocol.dispofast.modules.shipping.api.dtos.VehicleResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

  public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
    if (vehicle == null) {
      return null;
    }
    return new VehicleResponseDTO(
        vehicle.getId(),
        vehicle.getPlate(),
        vehicle.getState(),
        vehicle.getType(),
        vehicle.getCreatedAt());
  }
}
