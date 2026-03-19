package com.dispocol.dispofast.modules.pricelist.api.controllers;

import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import com.dispocol.dispofast.modules.pricelist.application.interfaces.PriceListService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/price-lists")
@RequiredArgsConstructor
public class PriceListController {

  private final PriceListService priceListService;

  @GetMapping
  @PreAuthorize("hasAuthority('PRICE_LISTS_VIEW')")
  public ResponseEntity<List<PriceListResponseDTO>> getAllPriceLists() {
    return ResponseEntity.ok(priceListService.getAllPriceLists());
  }

  @PostMapping("/{id}/upload")
  @PreAuthorize("hasAuthority('PRICE_LISTS_EDIT')")
  public ResponseEntity<Void> uploadPriceListItems(
      @PathVariable UUID id, @RequestParam("file") MultipartFile file) {
    priceListService.uploadPriceListItems(id, file);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/download")
  @PreAuthorize("hasAuthority('PRICE_LISTS_VIEW')")
  public ResponseEntity<byte[]> downloadPriceListFile(@PathVariable UUID id) {
    byte[] data = priceListService.downloadPriceListFile(id);
    String fileName = priceListService.getPriceListOriginalFileName(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(
        ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    return ResponseEntity.ok().headers(headers).body(data);
  }
}
