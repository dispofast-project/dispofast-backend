package com.dispocol.dispofast.modules.iam.api.dtos;

import com.dispocol.dispofast.modules.iam.domain.Role;
import jakarta.validation.constraints.Email;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequestDTO {

  private String name;

  @Email private String email;

  private String password;

  private Set<Role> roles;
}
