package com.dispocol.dispofast.modules.customers.api.dtos;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryResponseDTO {
  private List<PriceHistoryEntryDTO> entries;
  /** Precio vigente del producto en la lista de precios del cliente; null si no está en la lista. */
  private BigDecimal currentListPrice;
}
