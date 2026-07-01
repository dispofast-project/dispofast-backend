package com.dispocol.dispofast.modules.shipping.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarrierDTO {

  private String name;
  private String website;
}
