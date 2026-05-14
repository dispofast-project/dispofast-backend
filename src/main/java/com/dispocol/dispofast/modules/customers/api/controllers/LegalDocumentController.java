package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.LegalDocumentDTO;
import com.dispocol.dispofast.modules.customers.application.interfaces.LegalDocumentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/clients/{clientId}/legal-documents")
@RequiredArgsConstructor
public class LegalDocumentController {

  private final LegalDocumentService legalDocumentService;

  @GetMapping
  @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
  public ResponseEntity<List<LegalDocumentDTO>> getAll(@PathVariable UUID clientId) {
    return ResponseEntity.ok(legalDocumentService.getByClientId(clientId));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('CUSTOMERS_CREATE')")
  public ResponseEntity<LegalDocumentDTO> upload(
      @PathVariable UUID clientId, @RequestParam("file") MultipartFile file) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(legalDocumentService.upload(clientId, file));
  }

  @DeleteMapping("/{docId}")
  @PreAuthorize("hasAuthority('CUSTOMERS_CREATE')")
  public ResponseEntity<Void> delete(@PathVariable UUID clientId, @PathVariable UUID docId) {
    legalDocumentService.delete(clientId, docId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{docId}/download")
  @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
  public ResponseEntity<byte[]> download(@PathVariable UUID clientId, @PathVariable UUID docId) {
    byte[] data = legalDocumentService.download(docId);
    String filename = legalDocumentService.getFileName(docId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

    return ResponseEntity.ok().headers(headers).body(data);
  }
}
