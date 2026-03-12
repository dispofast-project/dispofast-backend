package com.dispocol.dispofast.modules.customers.infra.persistence;

import com.dispocol.dispofast.modules.customers.domain.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {}
