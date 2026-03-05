package com.dispocol.dispofast.modules.iam.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermission {

  @EmbeddedId private UserPermissionId id = new UserPermissionId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private AppUser user;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("permissionId")
  @JoinColumn(name = "permission_id")
  private Permission permission;

  @Column(nullable = false)
  private boolean granted = true;
}
