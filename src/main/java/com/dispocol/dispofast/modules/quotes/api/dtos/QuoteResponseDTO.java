package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
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
public class QuoteResponseDTO {

  private UUID id;
  private String number;
  private QuoteStatus status;
  private double subtotalAmount;
  private double discountTotal;
  private double taxTotal;
  private double totalAmount;
  private OffsetDateTime expirationDate;
  private UUID accountId;
  private String sellerName;
  private LocationDTO location;
  private UUID priceListId;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
