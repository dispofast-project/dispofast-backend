package com.dispocol.dispofast.modules.quotes.infra.persistence;

import com.dispocol.dispofast.modules.quotes.domain.Prospect;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProspectRepository extends JpaRepository<Prospect, UUID> {}
