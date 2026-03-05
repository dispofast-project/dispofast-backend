package com.dispocol.dispofast.modules.iam.api.dtos;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
  private String id;
  private String name;
  private String email;
  private String role;
  private Set<String> effectivePermissions;
}
