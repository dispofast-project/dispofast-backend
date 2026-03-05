package com.dispocol.dispofast.modules.iam.domain;

import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

  @Id @GeneratedValue @EqualsAndHashCode.Include private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "permissions")
  private Set<Role> roles = new HashSet<>();
}
