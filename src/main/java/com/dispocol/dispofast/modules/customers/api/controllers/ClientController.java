package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<Page<ClientPreviewDTO>> getAllClients(
      Pageable pageable,
      @RequestParam(required = false) String text,
      @RequestParam(required = false) String key,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) String city) {
    return ResponseEntity.ok(clientService.getAllClients(pageable, text, key, isActive, city));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable UUID id) {
    return ResponseEntity.ok(clientService.getClientById(id));
  }
}
