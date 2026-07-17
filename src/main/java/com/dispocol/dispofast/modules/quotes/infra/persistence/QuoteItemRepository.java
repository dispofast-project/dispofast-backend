package com.dispocol.dispofast.modules.quotes.infra.persistence;

import com.dispocol.dispofast.modules.quotes.domain.QuoteItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteItemRepository extends JpaRepository<QuoteItem, UUID> {

  List<QuoteItem> findByQuoteId(UUID quoteId);

  Optional<QuoteItem> findByQuote_IdAndProduct_Id(UUID quoteId, UUID productId);

  @Query(
      "SELECT qi FROM QuoteItem qi JOIN FETCH qi.quote q "
          + "WHERE q.account.id = :clientId AND qi.product.id = :productId "
          + "ORDER BY q.createdAt DESC")
  List<QuoteItem> findByAccountIdAndProductId(
      @Param("clientId") UUID clientId, @Param("productId") UUID productId);
}
