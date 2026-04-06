package com.dispocol.dispofast.modules.quotes.application.impl;

import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import com.dispocol.dispofast.modules.quotes.api.dtos.AddQuoteItemRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteItemResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteItemRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.mappers.QuoteItemMapper;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteItemService;
import com.dispocol.dispofast.modules.quotes.domain.QuoteItem;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuoteItemRepository;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteItemServiceImpl implements QuoteItemService {

  private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

  private final QuoteItemRepository quoteItemRepository;
  private final QuotesRepository quotesRepository;
  private final ProductRepository productRepository;
  private final QuoteItemMapper quoteItemMapper;
  private final QuoteServiceImpl quoteService;

  @Override
  @Transactional
  public QuoteItemResponseDTO addItem(UUID quoteId, AddQuoteItemRequestDTO dto) {
    Quotes quote = findQuote(quoteId);
    Product product = findProduct(dto.getProductId());

    QuoteItem item = buildItem(quote, product, dto.getQuantity(), dto.getUnitPrice());
    QuoteItem saved = quoteItemRepository.save(item);

    quoteService.recalculateQuoteTotals(quote);
    quotesRepository.save(quote);

    return quoteItemMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public QuoteItemResponseDTO updateItem(UUID quoteId, UUID itemId, UpdateQuoteItemRequestDTO dto) {
    Quotes quote = findQuote(quoteId);
    QuoteItem item =
        quoteItemRepository
            .findById(itemId)
            .filter(i -> i.getQuote().getId().equals(quoteId))
            .orElseThrow(
                () -> new ResourceNotFoundException("QuoteItem not found with id: " + itemId));

    if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
    if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());

    recalculateItemAmounts(item);
    QuoteItem saved = quoteItemRepository.save(item);

    quoteService.recalculateQuoteTotals(quote);
    quotesRepository.save(quote);

    return quoteItemMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public void removeItem(UUID quoteId, UUID itemId) {
    Quotes quote = findQuote(quoteId);
    QuoteItem item =
        quoteItemRepository
            .findById(itemId)
            .filter(i -> i.getQuote().getId().equals(quoteId))
            .orElseThrow(
                () -> new ResourceNotFoundException("QuoteItem not found with id: " + itemId));

    quoteItemRepository.delete(item);
    quoteService.recalculateQuoteTotals(quote);
    quotesRepository.save(quote);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuoteItemResponseDTO> getItems(UUID quoteId) {
    findQuote(quoteId);
    return quoteItemMapper.toResponseDTOList(quoteItemRepository.findByQuoteId(quoteId));
  }

  // ── Helpers ──────────────────────────────────────────────────

  private QuoteItem buildItem(
      Quotes quote, Product product, BigDecimal quantity, BigDecimal unitPrice) {
    QuoteItem item = new QuoteItem();
    item.setQuote(quote);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setUnitPrice(unitPrice);
    recalculateItemAmounts(item);
    return item;
  }

  private void recalculateItemAmounts(QuoteItem item) {
    BigDecimal taxRate =
        Boolean.TRUE.equals(item.getProduct().isTaxFree()) ? BigDecimal.ZERO : IVA_RATE;
    BigDecimal gross = item.getUnitPrice().multiply(item.getQuantity());
    BigDecimal taxAmount = gross.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

    item.setTaxRate(taxRate);
    item.setTaxAmount(taxAmount);
    item.setLineTotal(gross.add(taxAmount).setScale(2, RoundingMode.HALF_UP));
  }

  private Quotes findQuote(UUID quoteId) {
    return quotesRepository
        .findById(quoteId)
        .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + quoteId));
  }

  private Product findProduct(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Product not found with id: " + productId));
  }
}
