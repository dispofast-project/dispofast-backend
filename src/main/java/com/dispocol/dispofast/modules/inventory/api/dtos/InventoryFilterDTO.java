package com.dispocol.dispofast.modules.inventory.api.dtos;

import com.dispocol.dispofast.modules.inventory.domain.StockState;
import lombok.Data;

@Data
public class InventoryFilterDTO {

  private String search;
  private StockState state;
}
