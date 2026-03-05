package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOverrideDTO {
    
    @NotNull(message = "El id del permiso es obligatorio")
    private UUID permissionId;

    @NotNull(message = "El campo granted es obligatorio")
    private Boolean granted;
    
}
