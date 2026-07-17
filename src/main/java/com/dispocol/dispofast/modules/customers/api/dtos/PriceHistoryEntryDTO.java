package com.dispocol.dispofast.modules.customers.api.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryEntryDTO {
  private String source; // "ORDER" | "QUOTE"
  private String documentNumber;
  private OffsetDateTime date;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
}
