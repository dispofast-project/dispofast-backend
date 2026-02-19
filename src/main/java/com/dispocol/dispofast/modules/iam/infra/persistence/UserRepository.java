package com.dispocol.dispofast.modules.iam.infra.persistence;

import com.dispocol.dispofast.modules.iam.domain.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

  Page<AppUser> findByFullNameContainingIgnoreCase(String search, Pageable pageable);
}
