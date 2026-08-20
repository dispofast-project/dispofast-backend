package com.dispocol.dispofast.modules.purchases.api.dtos;

import com.dispocol.dispofast.modules.iam.api.dtos.UserPreview;
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
public class PurchaseOrderPreviewResponseDTO {
  private UUID id;
  private String number;
  private String supplierName;
  private UserPreview buyer;
  private OffsetDateTime createdAt;
  private BigDecimal total;
}
