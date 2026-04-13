package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateClientRequestDTO;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
  public ResponseEntity<Page<ClientPreviewDTO>> getAllClients(
      Pageable pageable,
      @RequestParam(required = false) String text,
      @RequestParam(required = false) String key,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) String city) {
    return ResponseEntity.ok(clientService.getAllClients(pageable, text, key, isActive, city));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('CUSTOMERS_VIEW')")
  public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable UUID id) {
    return ResponseEntity.ok(clientService.getClientById(id));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('CUSTOMERS_CREATE')")
  public ResponseEntity<ClientResponseDTO> createClient(
      @Valid @RequestPart("clientData") CreateClientRequestDTO request,
      @RequestPart(value = "documents", required = false) List<MultipartFile> documents
    ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request, documents));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClientResponseDTO> updateClient(
      @PathVariable UUID id, @Valid @RequestPart("clientData") CreateClientRequestDTO request,
      @RequestPart(value = "documents", required = false) List<MultipartFile> documents) {
    return ResponseEntity.ok(clientService.updateClient(id, request, documents));
  }
}
