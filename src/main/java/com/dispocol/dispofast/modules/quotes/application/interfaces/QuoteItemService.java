package com.dispocol.dispofast.modules.quotes.application.interfaces;

import com.dispocol.dispofast.modules.quotes.api.dtos.AddQuoteItemRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteItemResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteItemRequestDTO;
import java.util.List;
import java.util.UUID;

public interface QuoteItemService {

  QuoteItemResponseDTO addItem(UUID quoteId, AddQuoteItemRequestDTO dto);

  QuoteItemResponseDTO updateItem(UUID quoteId, UUID itemId, UpdateQuoteItemRequestDTO dto);

  void removeItem(UUID quoteId, UUID itemId);

  List<QuoteItemResponseDTO> getItems(UUID quoteId);
}
