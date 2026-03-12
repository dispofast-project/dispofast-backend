package com.dispocol.dispofast.modules.inventory.api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryResponseDTO {

  private int quantityAvailable;
  private int quantityReserved;
  private String state;
  private String productName;
}
