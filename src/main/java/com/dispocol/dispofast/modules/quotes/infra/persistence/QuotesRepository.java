package com.dispocol.dispofast.modules.quotes.infra.persistence;

import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotesRepository extends JpaRepository<Quotes, UUID> {}
