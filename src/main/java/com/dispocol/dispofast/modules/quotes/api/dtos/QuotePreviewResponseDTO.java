package com.dispocol.dispofast.modules.quotes.api.dtos;

import com.dispocol.dispofast.modules.iam.api.dtos.UserPreview;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import java.math.BigDecimal;
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
public class QuotePreviewResponseDTO {
  private UUID id;
  private String number;
  private QuoteStatus status;
  private String accountName;
  private UserPreview seller;
  private OffsetDateTime createdAt;
  private BigDecimal total;
  private OffsetDateTime expirationDate;
}
