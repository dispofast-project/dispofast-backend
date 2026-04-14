package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPermissionRequestDTO {
  @NotNull(message = "El campo de permisos es obligatorio")
  private Set<PermissionOverrideDTO> permissions;
}
