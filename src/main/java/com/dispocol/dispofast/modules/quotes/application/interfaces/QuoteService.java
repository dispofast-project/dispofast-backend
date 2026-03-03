package com.dispocol.dispofast.modules.quotes.application.interfaces;

import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteService {

  QuoteResponseDTO createQuote(CreateQuoteRequestDTO build);

  QuoteResponseDTO getQuoteById(UUID id);

  QuoteResponseDTO updateQuote(UUID id, UpdateQuoteRequestDTO build);

  Page<QuoteResponseDTO> getAllQuotes(Pageable pageable);
}
