package com.dispocol.dispofast.modules.quotes.application.impl;

import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuotePreviewResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.mappers.QuoteMapper;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteService;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.modules.temp.account.Person;
import com.dispocol.dispofast.modules.temp.account.infra.persistence.PersonRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.Location;
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
  private final LocationService locationService;
  private final PersonRepository personRepository;

  @Override
  @Transactional
  public QuoteResponseDTO createQuote(CreateQuoteRequestDTO createQuoteRequestDTO) {
    Quotes quotes = quoteMapper.toEntity(createQuoteRequestDTO);

    Person person =
        personRepository
            .findById(createQuoteRequestDTO.getAccountId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Client not found with id: " + createQuoteRequestDTO.getAccountId()));

    quotes.setAccount(person);
    quotes.setNumber("QT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    quotes.setStatus(QuoteStatus.PENDING);
    quotes.setSubtotalAmount(0.0);
    quotes.setDiscountTotal(0.0);
    quotes.setTaxTotal(0.0);
    quotes.setTotalAmount(0.0);
    quotes.setExpirationDate(OffsetDateTime.now().plusDays(30));

    if (person.getOrganization() != null) {
      quotes.setLocation(person.getOrganization().getLocation());
      quotes.setPriceList(person.getOrganization().getPriceList());
      quotes.setSeller(person.getOrganization().getAssignedSalesRep());
    }

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

    if (updateQuoteRequestDTO.getLocationId() != null) {
      Location location = locationService.findEntityById(updateQuoteRequestDTO.getLocationId());
      quotes.setLocation(location);
    }

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
