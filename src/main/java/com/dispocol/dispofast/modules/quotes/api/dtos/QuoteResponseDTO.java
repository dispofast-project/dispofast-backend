package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.modules.quotes.domain.OfferValidity;
import com.dispocol.dispofast.modules.quotes.domain.PaymentCondition;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
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
  private PaymentCondition paymentCondition;
  private OfferValidity offerValidity;
  private ClientResponseDTO account;
  private UUID sellerId;
  private String sellerName;
  private CityDTO location;
  private PriceListResponseDTO priceList;
  private List<QuoteItemResponseDTO> items;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  // ── Detalles de pago ─────────────────────────────────────────
  private BigDecimal subtotalAmount;
  private BigDecimal commercialDiscountRate;
  private BigDecimal commercialDiscountAmount;
  private BigDecimal otherDiscountsRate;
  private BigDecimal otherDiscountsAmount;
  private BigDecimal ivaRate;
  private BigDecimal ivaAmount;
  private BigDecimal retefuenteRate;
  private BigDecimal retefuenteAmount;
  private BigDecimal totalAmount;
}
