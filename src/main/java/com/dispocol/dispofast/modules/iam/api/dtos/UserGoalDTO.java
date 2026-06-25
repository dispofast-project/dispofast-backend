package com.dispocol.dispofast.modules.iam.api.dtos;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGoalDTO {
  private UUID id;
  private String type;
  private int month;
  private int year;
  private BigDecimal value;
}
