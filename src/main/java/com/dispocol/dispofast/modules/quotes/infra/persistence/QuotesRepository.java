package com.dispocol.dispofast.modules.quotes.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dispocol.dispofast.modules.quotes.domain.Quotes;

public interface QuotesRepository extends JpaRepository<Quotes, UUID> {

}
