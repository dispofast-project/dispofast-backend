package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequestDTO {

  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
  private String name;

  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "El email debe ser válido")
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  private String password;

  @NotNull(message = "El rol es obligatorio")
  private UUID roleId;
}
