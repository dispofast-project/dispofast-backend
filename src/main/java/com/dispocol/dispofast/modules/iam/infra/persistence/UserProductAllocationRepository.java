package com.dispocol.dispofast.modules.iam.infra.persistence;

import com.dispocol.dispofast.modules.iam.domain.UserProductAllocation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductAllocationRepository
    extends JpaRepository<UserProductAllocation, UUID> {

  List<UserProductAllocation> findByUserId(UUID userId);

  Optional<UserProductAllocation> findByUser_IdAndProduct_Id(UUID userId, UUID productId);
}
