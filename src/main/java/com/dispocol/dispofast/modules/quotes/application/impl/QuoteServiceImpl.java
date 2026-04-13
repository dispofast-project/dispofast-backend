package com.dispocol.dispofast.modules.quotes.application.impl;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import com.dispocol.dispofast.modules.quotes.api.dtos.ChangeQuoteStatusRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuotePreviewResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.mappers.QuoteMapper;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteService;
import com.dispocol.dispofast.modules.quotes.domain.QuoteItem;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuoteItemRepository;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.params.infra.persistence.SystemParamRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

  private static final BigDecimal RETEFUENTE_RATE = new BigDecimal("0.035");
  private static final BigDecimal RETEICA_RATE = new BigDecimal("0.005");

  private final QuotesRepository quotesRepository;
  private final QuoteItemRepository quoteItemRepository;
  private final QuoteMapper quoteMapper;
  private final ClientRepository clientRepository;
  private final PriceListRepository priceListRepository;
  private final UserRepository userRepository;
  private final SystemParamRepository systemParamRepository;

  @Override
  @Transactional
  public QuoteResponseDTO createQuote(CreateQuoteRequestDTO dto) {
    Client client =
        clientRepository
            .findById(dto.getAccountId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Client not found with id: " + dto.getAccountId()));

    Quotes quote = quoteMapper.toEntity(dto);
    quote.setAccount(client);
    quote.setNumber("QT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    quote.setStatus(QuoteStatus.PENDING);
    quote.setPriceList(client.getPriceList());
    quote.setSeller(client.getDefaultAdvisor());
    quote.setCity(client.getCity());
    quote.setZone(client.getZone());

    // Tasas iniciales: IVA leído de system_params
    BigDecimal ivaRate =
        systemParamRepository
            .findByClave("IVA")
            .map(p -> p.getValor())
            .orElse(new BigDecimal("0.19"));
    quote.setIvaRate(ivaRate);

    Integer defaultDiscount = client.getDefaultDiscountRate();
    BigDecimal commRate =
        defaultDiscount != null
            ? new BigDecimal(defaultDiscount).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    quote.setCommercialDiscountRate(commRate);

    // Retenciones solo para personas jurídicas
    if (!(client instanceof Individual) && Boolean.TRUE.equals(client.getRetefuenteApplies())) {
      quote.setRetefuenteRate(RETEFUENTE_RATE);
    }
    if (!(client instanceof Individual)) {
      quote.setReteicaRate(RETEICA_RATE);
    }

    // Montos en cero hasta que se agreguen ítems
    quote.setSubtotalAmount(BigDecimal.ZERO);
    quote.setCommercialDiscountAmount(BigDecimal.ZERO);
    quote.setOtherDiscountsRate(BigDecimal.ZERO);
    quote.setOtherDiscountsAmount(BigDecimal.ZERO);
    quote.setIvaAmount(BigDecimal.ZERO);
    quote.setRetefuenteAmount(quote.getRetefuenteRate() != null ? BigDecimal.ZERO : null);
    quote.setReteicaAmount(quote.getReteicaRate() != null ? BigDecimal.ZERO : null);
    quote.setTotalAmount(BigDecimal.ZERO);

    return quoteMapper.toResponseDTO(quotesRepository.save(quote));
  }

  @Override
  @Transactional(readOnly = true)
  public QuoteResponseDTO getQuoteById(UUID id) {
    return quoteMapper.toResponseDTO(findQuote(id));
  }

  @Override
  @Transactional
  public QuoteResponseDTO updateQuote(UUID id, UpdateQuoteRequestDTO dto) {
    Quotes quote = findQuote(id);
    quoteMapper.updateEntityFromDTO(dto, quote);
    if (dto.getPriceListId() != null) {
      quote.setPriceList(priceListRepository.getReferenceById(dto.getPriceListId()));
    }
    if (dto.getSellerId() != null) {
      quote.setSeller(userRepository.getReferenceById(dto.getSellerId()));
    }
    recalculateQuoteTotals(quote);
    return quoteMapper.toResponseDTO(quotesRepository.save(quote));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QuotePreviewResponseDTO> getAllQuotes(String text, String key, Pageable pageable) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    Page<Quotes> page;
    if (isAdmin) {
      if (text != null && !text.isBlank()) {
        String effectiveKey = (key != null && !key.isBlank()) ? key.trim().toLowerCase() : "";
        page = quotesRepository.searchByText(text.trim(), effectiveKey, pageable);
      } else {
        page = quotesRepository.findAll(pageable);
      }
    } else {
      AppUser currentUser =
          userRepository
              .findByEmailIgnoreCase(auth.getName())
              .orElseThrow(
                  () -> new ResourceNotFoundException("User not found: " + auth.getName()));
      UUID sellerId = currentUser.getId();
      if (text != null && !text.isBlank()) {
        String effectiveKey = (key != null && !key.isBlank()) ? key.trim().toLowerCase() : "";
        page =
            quotesRepository.searchByTextAndSeller(text.trim(), effectiveKey, sellerId, pageable);
      } else {
        page = quotesRepository.findBySellerId(sellerId, pageable);
      }
    }
    return page.map(quoteMapper::toPreviewResponseDTO);
  }

  @Override
  @Transactional
  public QuoteResponseDTO changeStatus(UUID id, ChangeQuoteStatusRequestDTO dto) {
    Quotes quote = findQuote(id);
    quote.setStatus(dto.getStatus());
    return quoteMapper.toResponseDTO(quotesRepository.save(quote));
  }

  // ── Lógica de recálculo ──────────────────────────────────────

  void recalculateQuoteTotals(Quotes quote) {
    List<QuoteItem> items = quoteItemRepository.findByQuoteId(quote.getId());

    BigDecimal subtotal =
        items.stream()
            .map(i -> i.getUnitPrice().multiply(i.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal ivaTotal =
        items.stream()
            .map(QuoteItem::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal commDiscountAmount =
        subtotal
            .multiply(
                quote.getCommercialDiscountRate() != null
                    ? quote.getCommercialDiscountRate()
                    : BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal otherDiscRate =
        quote.getOtherDiscountsRate() != null ? quote.getOtherDiscountsRate() : BigDecimal.ZERO;
    BigDecimal otherDiscAmount = subtotal.multiply(otherDiscRate).setScale(2, RoundingMode.HALF_UP);

    BigDecimal netBase = subtotal.subtract(commDiscountAmount).subtract(otherDiscAmount);

    BigDecimal retefuenteAmount = null;
    if (quote.getRetefuenteRate() != null
        && quote.getRetefuenteRate().compareTo(BigDecimal.ZERO) > 0) {
      retefuenteAmount =
          netBase.multiply(quote.getRetefuenteRate()).setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal reteicaAmount = null;
    if (quote.getReteicaRate() != null && quote.getReteicaRate().compareTo(BigDecimal.ZERO) > 0) {
      reteicaAmount = netBase.multiply(quote.getReteicaRate()).setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal total =
        netBase
            .add(ivaTotal)
            .subtract(retefuenteAmount != null ? retefuenteAmount : BigDecimal.ZERO)
            .subtract(reteicaAmount != null ? reteicaAmount : BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

    quote.setSubtotalAmount(subtotal);
    quote.setIvaAmount(ivaTotal);
    quote.setCommercialDiscountAmount(commDiscountAmount);
    quote.setOtherDiscountsAmount(otherDiscAmount);
    quote.setRetefuenteAmount(retefuenteAmount);
    quote.setReteicaAmount(reteicaAmount);
    quote.setTotalAmount(total);
  }

  private Quotes findQuote(UUID id) {
    return quotesRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
  }
}
