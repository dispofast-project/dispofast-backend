package com.dispocol.dispofast.modules.shipping.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarrierResponseDTO {

  private UUID id;
  private String name;
  private String plate;
}
