package com.dispocol.dispofast.modules.iam.infra.persistence;

import com.dispocol.dispofast.modules.iam.domain.UserCommissionRate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommissionRateRepository extends JpaRepository<UserCommissionRate, UUID> {

  List<UserCommissionRate> findByUserId(UUID userId);
}
