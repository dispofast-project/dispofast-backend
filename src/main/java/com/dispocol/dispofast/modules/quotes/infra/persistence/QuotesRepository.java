package com.dispocol.dispofast.modules.quotes.infra.persistence;

import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuotesRepository extends JpaRepository<Quotes, UUID> {

  /**
   * Searches quotes by text using partial, case-insensitive matching. The {@code key} parameter
   * determines which field to search:
   *
   * <ul>
   *   <li>{@code "seller"} — matches against the seller's full name
   *   <li>{@code "number"} — matches against the quote number
   *   <li>{@code "client"} — matches against the client's identification number
   *   <li>Any other value / null — searches across all three fields
   * </ul>
   */
  @Query(
      """
      SELECT q FROM Quotes q
      WHERE
        CASE :key
          WHEN 'seller' THEN LOWER(q.seller.fullName)
          WHEN 'number' THEN LOWER(q.number)
          WHEN 'client' THEN LOWER(q.account.identificationNumber)
          ELSE ''
        END LIKE LOWER(CONCAT('%', :text, '%'))
        OR (:key NOT IN ('seller', 'number', 'client') AND (
              LOWER(q.seller.fullName)              LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(q.number)                      LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(q.account.identificationNumber) LIKE LOWER(CONCAT('%', :text, '%'))
        ))
      """)
  Page<Quotes> searchByText(
      @Param("text") String text, @Param("key") String key, Pageable pageable);
}
