package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPermissionRequestDTO {

  private Set<PermissionOverrideDTO> permissions;
}
