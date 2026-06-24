package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {
  private UUID id;
  private String name;
  private Set<PermissionSummaryDTO> permissions;
}
