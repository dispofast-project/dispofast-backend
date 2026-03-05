package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequestDTO {
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Size(max = 70, message = "El email no puede tener más de 70 caracteres")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private UUID roleId;
}
