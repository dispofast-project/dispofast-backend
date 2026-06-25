package com.dispocol.dispofast.modules.iam.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommissionRateDTO {
  private UUID id;
  private UUID categoryId;
  private String categoryName;
  private BigDecimal rate;
}
