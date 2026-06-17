package com.dispocol.dispofast.modules.shipping.api.mappers;

import com.dispocol.dispofast.modules.shipping.api.dtos.DriverResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

  public DriverResponseDTO toResponseDTO(Driver driver) {
    if (driver == null) {
      return null;
    }
    return new DriverResponseDTO(
        driver.getId(),
        driver.getName(),
        driver.getPhone(),
        driver.getCedula(),
        driver.getCreatedAt());
  }
}
