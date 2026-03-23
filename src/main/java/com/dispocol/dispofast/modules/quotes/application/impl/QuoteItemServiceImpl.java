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

  private static final BigDecimal TAX_RATE = new BigDecimal("0.19");

  private final QuoteItemRepository quoteItemRepository;
  private final QuotesRepository quotesRepository;
  private final ProductRepository productRepository;
  private final QuoteItemMapper quoteItemMapper;

  @Override
  @Transactional
  public QuoteItemResponseDTO addItem(UUID quoteId, AddQuoteItemRequestDTO dto) {
    Quotes quote = findQuote(quoteId);
    Product product = findProduct(dto.getProductId());

    BigDecimal discount =
        dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
    BigDecimal subtotal = dto.getUnitPrice().multiply(dto.getQuantity());
    BigDecimal taxableBase = subtotal.subtract(discount);
    BigDecimal tax =
        product.isTaxFree()
            ? BigDecimal.ZERO
            : taxableBase.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

    QuoteItem item = new QuoteItem();
    item.setQuote(quote);
    item.setProduct(product);
    item.setQuantity(dto.getQuantity());
    item.setUnitPrice(dto.getUnitPrice());
    item.setDiscountAmount(discount);
    item.setTaxAmount(tax);
    item.setLineTotal(taxableBase.add(tax));

    QuoteItem saved = quoteItemRepository.save(item);
    recalculateQuoteTotals(quote);
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
    if (dto.getDiscountAmount() != null) item.setDiscountAmount(dto.getDiscountAmount());

    BigDecimal subtotal = item.getUnitPrice().multiply(item.getQuantity());
    BigDecimal taxableBase = subtotal.subtract(item.getDiscountAmount());
    BigDecimal tax =
        item.getProduct().isTaxFree()
            ? BigDecimal.ZERO
            : taxableBase.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    item.setTaxAmount(tax);
    item.setLineTotal(taxableBase.add(tax));

    QuoteItem saved = quoteItemRepository.save(item);
    recalculateQuoteTotals(quote);
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
    recalculateQuoteTotals(quote);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuoteItemResponseDTO> getItems(UUID quoteId) {
    findQuote(quoteId);
    return quoteItemMapper.toResponseDTOList(quoteItemRepository.findByQuoteId(quoteId));
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

  private void recalculateQuoteTotals(Quotes quote) {
    List<QuoteItem> items = quoteItemRepository.findByQuoteId(quote.getId());

    double subtotal =
        items.stream()
            .mapToDouble(i -> i.getUnitPrice().multiply(i.getQuantity()).doubleValue())
            .sum();
    double discountTotal =
        items.stream().mapToDouble(i -> i.getDiscountAmount().doubleValue()).sum();
    double taxTotal = items.stream().mapToDouble(i -> i.getTaxAmount().doubleValue()).sum();

    quote.setSubtotalAmount(subtotal);
    quote.setDiscountTotal(discountTotal);
    quote.setTaxTotal(taxTotal);
    quote.setTotalAmount(subtotal - discountTotal + taxTotal);
    quotesRepository.save(quote);
  }
}
