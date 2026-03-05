package com.dispocol.dispofast.modules.quotes.application.impl;

import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuotePreviewResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.mappers.QuoteMapper;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteService;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

  private final QuotesRepository quotesRepository;
  private final QuoteMapper quoteMapper;
  private final ClientRepository clientRepository;

  @Override
  @Transactional
  public QuoteResponseDTO createQuote(CreateQuoteRequestDTO createQuoteRequestDTO) {
    Quotes quotes = quoteMapper.toEntity(createQuoteRequestDTO);

    Client client =
        clientRepository
            .findById(createQuoteRequestDTO.getAccountId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Client not found with id: " + createQuoteRequestDTO.getAccountId()));

    quotes.setAccount(client);
    quotes.setNumber("QT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    quotes.setStatus(QuoteStatus.PENDING);
    quotes.setSubtotalAmount(0.0);
    quotes.setDiscountTotal(0.0);
    quotes.setTaxTotal(0.0);
    quotes.setTotalAmount(0.0);
    quotes.setExpirationDate(OffsetDateTime.now().plusDays(30));

    quotes.setPriceList(client.getPriceList());
    quotes.setSeller(client.getDefaultAdvisor());

    Quotes savedQuotes = quotesRepository.save(quotes);
    return quoteMapper.toResponseDTO(savedQuotes);
  }

  @Override
  @Transactional(readOnly = true)
  public QuoteResponseDTO getQuoteById(UUID id) {
    Quotes quotes =
        quotesRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
    return quoteMapper.toResponseDTO(quotes);
  }

  @Override
  @Transactional
  public QuoteResponseDTO updateQuote(UUID id, UpdateQuoteRequestDTO updateQuoteRequestDTO) {
    Quotes quotes =
        quotesRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));

    quoteMapper.updateEntityFromDTO(updateQuoteRequestDTO, quotes);

    Quotes updatedQuotes = quotesRepository.save(quotes);
    return quoteMapper.toResponseDTO(updatedQuotes);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QuotePreviewResponseDTO> getAllQuotes(String text, String key, Pageable pageable) {
    Page<Quotes> quotesPage;
    if (text != null && !text.isBlank()) {
      String effectiveKey = (key != null && !key.isBlank()) ? key.trim().toLowerCase() : "";
      quotesPage = quotesRepository.searchByText(text.trim(), effectiveKey, pageable);
    } else {
      quotesPage = quotesRepository.findAll(pageable);
    }
    return quotesPage.map(quoteMapper::toPreviewResponseDTO);
  }
}
