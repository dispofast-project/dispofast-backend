package com.dispocol.dispofast.modules.iam.domain;

import com.dispocol.dispofast.modules.customers.domain.Client;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

  @Id @GeneratedValue @EqualsAndHashCode.Include private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, name = "password_hash", length = 255)
  private String passwordHash;

  @Column(nullable = false, name = "full_name", length = 255)
  private String fullName;

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false, name = "created_at")
  private OffsetDateTime createdAt;

  @Column(nullable = false, name = "updated_at")
  private OffsetDateTime updatedAt;

  @OneToMany(mappedBy = "defaultAdvisor")
  private Set<Client> customers = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private Set<UserPermission> permissions = new HashSet<>();

  public void addCustomer(Client customer) {
    customers.add(customer);
    customer.setDefaultAdvisor(this);
  }

  public void removeCustomer(Client customer) {
    customers.remove(customer);
    customer.setDefaultAdvisor(null);
  }

  @PrePersist
  void prePersist() {
    OffsetDateTime now = OffsetDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }

  public Set<String> resolveEffectivePermissionNames() {
    Set<String> effectivePermissions = new HashSet<>();

    for (Role role : roles) {
      for (Permission permission : role.getPermissions()) {
        effectivePermissions.add(permission.getName());
      }
    }

    for (UserPermission override : permissions) {

      String name = override.getPermission().getName();

      if (override.isGranted()) {
        effectivePermissions.add(name);
      } else {
        effectivePermissions.remove(name);
      }
    }

    return effectivePermissions;
  }
}
