package com.dispocol.dispofast.modules.invoices.api.controllers;

import com.dispocol.dispofast.modules.invoices.api.dtos.InvoiceResponseDTO;
import com.dispocol.dispofast.modules.invoices.application.interfaces.InvoiceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

  private final InvoiceService invoiceService;

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_VIEW')")
  public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(invoiceService.getById(id));
  }

  @GetMapping("/{id}/download")
  @PreAuthorize("hasAuthority('PURCHASE_ORDERS_VIEW')")
  public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
    byte[] data = invoiceService.downloadPdf(id);
    String fileName = invoiceService.getPdfFileName(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    headers.setContentType(MediaType.APPLICATION_PDF);
    return ResponseEntity.ok().headers(headers).body(data);
  }
}
