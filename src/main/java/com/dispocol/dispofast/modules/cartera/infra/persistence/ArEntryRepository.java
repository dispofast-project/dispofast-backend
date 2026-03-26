package com.dispocol.dispofast.modules.cartera.infra.persistence;

import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArEntryRepository
    extends JpaRepository<ArEntry, UUID>, JpaSpecificationExecutor<ArEntry> {}
