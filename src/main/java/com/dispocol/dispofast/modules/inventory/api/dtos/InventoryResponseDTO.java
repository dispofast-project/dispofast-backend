package com.dispocol.dispofast.modules.inventory.api.dtos;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryResponseDTO {

  private UUID productId;
  private int quantityAvailable;
  private int quantityReserved;
  private String state;
  private String productName;
}
