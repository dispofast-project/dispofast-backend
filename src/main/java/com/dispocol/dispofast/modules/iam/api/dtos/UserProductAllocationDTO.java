package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProductAllocationDTO {
  private UUID id;
  private UUID productId;
  private String productName;
  private String productSku;
  private int assignedQuantity;
  private int consumedQuantity;
  private int remainingQuantity;
}
