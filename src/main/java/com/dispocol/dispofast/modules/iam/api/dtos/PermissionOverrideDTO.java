package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOverrideDTO {

  @NotNull(message = "El id del permiso es obligatorio")
  private UUID permissionId;

  @NotNull(message = "El nombre del permiso es obligatorio")
  private String permissionName;

  @NotNull(message = "El campo granted es obligatorio")
  private Boolean granted;
}
