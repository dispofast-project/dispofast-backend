package com.dispocol.dispofast.modules.quotes.infra.persistence;

import com.dispocol.dispofast.modules.quotes.domain.QuoteItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, UUID> {

  List<QuoteItem> findByQuoteId(UUID quoteId);

  Optional<QuoteItem> findByQuote_IdAndProduct_Id(UUID quoteId, UUID productId);
}
