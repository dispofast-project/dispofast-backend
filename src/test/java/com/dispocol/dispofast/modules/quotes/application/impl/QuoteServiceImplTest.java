package com.dispocol.dispofast.modules.quotes.application.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.mappers.QuoteMapper;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.Location;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceImplTest {

  @Mock private QuotesRepository quotesRepository;

  @Mock private QuoteMapper quoteMapper;

  @Mock private LocationService locationService;

  @InjectMocks private QuoteServiceImpl quoteService;

  @Test
  @DisplayName("Should create quote successfully")
  public void shouldCreateQuote() {
    // Given
    CreateQuoteRequestDTO requestDTO = new CreateQuoteRequestDTO();
    Quotes quoteEntity = new Quotes();
    Quotes savedQuote = new Quotes();
    savedQuote.setId(UUID.randomUUID());
    QuoteResponseDTO responseDTO = new QuoteResponseDTO();
    responseDTO.setId(savedQuote.getId());

    requestDTO.setLocationId("LOC-001");
    Location location = new Location();
    location.setCityCode("LOC-001");

    when(quoteMapper.toEntity(requestDTO)).thenReturn(quoteEntity);
    when(locationService.findEntityById("LOC-001")).thenReturn(location);
    when(quotesRepository.save(quoteEntity)).thenReturn(savedQuote);
    when(quoteMapper.toResponseDTO(savedQuote)).thenReturn(responseDTO);

    // When
    QuoteResponseDTO result = quoteService.createQuote(requestDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(savedQuote.getId());
    verify(locationService).findEntityById("LOC-001");
    verify(quotesRepository).save(quoteEntity);
    verify(quoteMapper).toEntity(requestDTO);
    verify(quoteMapper).toResponseDTO(savedQuote);
  }

  @Test
  @DisplayName("Should get quote by id successfully")
  public void shouldGetQuoteById() {
    // Given
    UUID id = UUID.randomUUID();
    Quotes quote = new Quotes();
    quote.setId(id);
    QuoteResponseDTO responseDTO = new QuoteResponseDTO();
    responseDTO.setId(id);

    when(quotesRepository.findById(id)).thenReturn(Optional.of(quote));
    when(quoteMapper.toResponseDTO(quote)).thenReturn(responseDTO);

    // When
    QuoteResponseDTO result = quoteService.getQuoteById(id);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    verify(quotesRepository).findById(id);
    verify(quoteMapper).toResponseDTO(quote);
  }

  @Test
  @DisplayName("Should throw exception when getting quote by non-existent id")
  public void shouldThrowExceptionWhenGettingQuoteByNonExistentId() {
    // Given
    UUID id = UUID.randomUUID();
    when(quotesRepository.findById(id)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> quoteService.getQuoteById(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Quote not found with id: " + id);

    verify(quotesRepository).findById(id);
  }

  @Test
  @DisplayName("Should update quote successfully")
  public void shouldUpdateQuote() {
    // Given
    UUID id = UUID.randomUUID();
    UpdateQuoteRequestDTO requestDTO = new UpdateQuoteRequestDTO();
    Quotes existingQuote = new Quotes();
    existingQuote.setId(id);

    // Mocking behavior
    when(quotesRepository.findById(id)).thenReturn(Optional.of(existingQuote));

    // We simulate that updating runs without issues
    doNothing().when(quoteMapper).updateEntityFromDTO(requestDTO, existingQuote);

    // Test updating location as well
    requestDTO.setLocationId("LOC-002");
    Location location = new Location();
    location.setCityCode("LOC-002");
    when(locationService.findEntityById("LOC-002")).thenReturn(location);

    // After potential update, save is called
    when(quotesRepository.save(existingQuote)).thenReturn(existingQuote);

    QuoteResponseDTO responseDTO = new QuoteResponseDTO();
    responseDTO.setId(id);
    when(quoteMapper.toResponseDTO(existingQuote)).thenReturn(responseDTO);

    // When
    QuoteResponseDTO result = quoteService.updateQuote(id, requestDTO);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);

    verify(quotesRepository).findById(id);
    verify(quoteMapper).updateEntityFromDTO(requestDTO, existingQuote);
    verify(locationService).findEntityById("LOC-002");
    verify(quotesRepository).save(existingQuote);
    verify(quoteMapper).toResponseDTO(existingQuote);
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent quote")
  public void shouldThrowExceptionWhenUpdatingNonExistentQuote() {
    // Given
    UUID id = UUID.randomUUID();
    UpdateQuoteRequestDTO requestDTO = new UpdateQuoteRequestDTO();
    when(quotesRepository.findById(id)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> quoteService.updateQuote(id, requestDTO))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Quote not found with id: " + id);

    verify(quotesRepository).findById(id);
  }

  @Test
  @DisplayName("Should get all quotes")
  public void shouldGetAllQuotes() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    Quotes quote = new Quotes();
    List<Quotes> quotesList = Collections.singletonList(quote);
    Page<Quotes> quotesPage = new PageImpl<>(quotesList, pageable, quotesList.size());

    QuoteResponseDTO responseDTO = new QuoteResponseDTO();

    when(quotesRepository.findAll(pageable)).thenReturn(quotesPage);
    when(quoteMapper.toResponseDTO(quote)).thenReturn(responseDTO);

    // When
    Page<QuoteResponseDTO> result = quoteService.getAllQuotes(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    verify(quotesRepository).findAll(pageable);
    verify(quoteMapper).toResponseDTO(quote);
  }
}
