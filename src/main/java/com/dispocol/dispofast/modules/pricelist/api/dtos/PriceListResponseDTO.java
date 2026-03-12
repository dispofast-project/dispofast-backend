package com.dispocol.dispofast.modules.pricelist.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceListResponseDTO {
  private UUID id;
  private String name;
}
