package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
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
  private ClientResponseDTO account;
  private String sellerName;
  private CityDTO location;
  private PriceListResponseDTO priceList;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
