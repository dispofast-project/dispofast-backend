package com.dispocol.dispofast.modules.inventory.api.dtos;

import com.dispocol.dispofast.modules.inventory.domain.StockState;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryResponseDTO {

  private UUID productId;
  private String productName;
  private int quantityAvailable;
  private int quantityReserved;
  private StockState state;
}
