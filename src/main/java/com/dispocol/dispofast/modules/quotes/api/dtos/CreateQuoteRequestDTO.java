package com.dispocol.dispofast.modules.quotes.api.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuoteRequestDTO {

    private String number;
    private QuoteStatus status;
    private double subtotalAmount;
    private double discountTotal;
    private double taxTotal;
    private double totalAmount;
    private OffsetDateTime expirationDate;
    private UUID accountId;
    private UUID sellerId;
    private String locationId;
    private UUID priceListId;
}
