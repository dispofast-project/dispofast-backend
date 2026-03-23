package com.dispocol.dispofast.modules.quotes.api.controllers;

import com.dispocol.dispofast.modules.quotes.api.dtos.AddQuoteItemRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteItemResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteItemRequestDTO;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteItemService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes/{quoteId}/items")
@RequiredArgsConstructor
public class QuoteItemController {

  private final QuoteItemService quoteItemService;

  @PostMapping
  public ResponseEntity<QuoteItemResponseDTO> addItem(
      @PathVariable UUID quoteId, @RequestBody AddQuoteItemRequestDTO dto) {
    return new ResponseEntity<>(quoteItemService.addItem(quoteId, dto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<QuoteItemResponseDTO>> getItems(@PathVariable UUID quoteId) {
    return ResponseEntity.ok(quoteItemService.getItems(quoteId));
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<QuoteItemResponseDTO> updateItem(
      @PathVariable UUID quoteId,
      @PathVariable UUID itemId,
      @RequestBody UpdateQuoteItemRequestDTO dto) {
    return ResponseEntity.ok(quoteItemService.updateItem(quoteId, itemId, dto));
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> removeItem(@PathVariable UUID quoteId, @PathVariable UUID itemId) {
    quoteItemService.removeItem(quoteId, itemId);
    return ResponseEntity.noContent().build();
  }
}
