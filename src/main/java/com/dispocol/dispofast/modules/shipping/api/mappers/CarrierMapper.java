package com.dispocol.dispofast.modules.shipping.api.mappers;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import org.springframework.stereotype.Component;

@Component
public class CarrierMapper {

  public CarrierResponseDTO toResponseDTO(Carrier carrier) {
    if (carrier == null) {
      return null;
    }
    return new CarrierResponseDTO(carrier.getId(), carrier.getName(), carrier.getPlate());
  }

}
