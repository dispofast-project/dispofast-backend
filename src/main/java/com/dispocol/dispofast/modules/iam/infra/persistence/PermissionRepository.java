package com.dispocol.dispofast.modules.iam.infra.persistence;

import com.dispocol.dispofast.modules.iam.domain.Permission;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

  Optional<Permission> findByNameIgnoreCase(String name);

  List<Permission> findAllByNameIn(Set<String> names);
}
