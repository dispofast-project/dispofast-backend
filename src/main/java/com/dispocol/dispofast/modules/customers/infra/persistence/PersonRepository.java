package com.dispocol.dispofast.modules.customers.infra.persistence;

import com.dispocol.dispofast.modules.customers.domain.Individual;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Individual, UUID> {}
