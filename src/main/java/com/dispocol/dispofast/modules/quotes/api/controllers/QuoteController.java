package com.dispocol.dispofast.modules.quotes.api.controllers;

import com.dispocol.dispofast.modules.quotes.api.dtos.ChangeQuoteStatusRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.CreateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuotePreviewResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteResponseDTO;
import com.dispocol.dispofast.modules.quotes.api.dtos.UpdateQuoteRequestDTO;
import com.dispocol.dispofast.modules.quotes.application.interfaces.QuoteService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {

  private final QuoteService quoteService;

  @PostMapping
  public ResponseEntity<QuoteResponseDTO> createQuote(
      @RequestBody CreateQuoteRequestDTO createQuoteRequestDTO) {
    return new ResponseEntity<>(
        quoteService.createQuote(createQuoteRequestDTO), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<QuoteResponseDTO> getQuoteById(@PathVariable UUID id) {
    return ResponseEntity.ok(quoteService.getQuoteById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<QuoteResponseDTO> updateQuote(
      @PathVariable UUID id, @RequestBody UpdateQuoteRequestDTO updateQuoteRequestDTO) {
    return ResponseEntity.ok(quoteService.updateQuote(id, updateQuoteRequestDTO));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<QuoteResponseDTO> changeStatus(
      @PathVariable UUID id, @RequestBody ChangeQuoteStatusRequestDTO dto) {
    return ResponseEntity.ok(quoteService.changeStatus(id, dto));
  }

  @GetMapping
  public ResponseEntity<Page<QuotePreviewResponseDTO>> getAllQuotes(
      @RequestParam(required = false) String text,
      @RequestParam(required = false) String key,
      Pageable pageable) {
    return ResponseEntity.ok(quoteService.getAllQuotes(text, key, pageable));
  }
}
