package com.dispocol.dispofast.modules.iam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionId implements Serializable {

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "permission_id")
  private UUID permissionId;
}
