package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuoteRequestDTO {
  private QuoteStatus status;
  private Double subtotalAmount;
  private Double discountTotal;
  private Double taxTotal;
  private OffsetDateTime expirationDate;
  private UUID sellerId;
  private String locationId;
  private UUID priceListId;
}
