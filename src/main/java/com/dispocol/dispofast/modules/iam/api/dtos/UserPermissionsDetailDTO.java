package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionsDetailDTO {

  private String userId;
  private String userName;
  private String role;
  private Set<PermissionOverrideDTO> overrides;
}
