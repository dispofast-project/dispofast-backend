package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionSummaryDTO {
  private UUID id;
  private String name;
  private boolean grantedByRole;
}
