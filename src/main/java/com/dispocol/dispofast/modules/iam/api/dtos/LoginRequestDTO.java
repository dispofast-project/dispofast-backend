package com.dispocol.dispofast.modules.iam.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  @NotBlank
  @Size(min = 8, max = 100)
  private String password;
}
