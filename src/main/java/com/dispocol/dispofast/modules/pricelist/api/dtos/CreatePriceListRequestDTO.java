package com.dispocol.dispofast.modules.pricelist.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePriceListRequestDTO {

  @NotBlank(message = "El nombre de la lista de precios es obligatorio")
  private String name;
}
