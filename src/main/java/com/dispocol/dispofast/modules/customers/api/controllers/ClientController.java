package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<Page<ClientPreviewDTO>> getAllClients(Pageable pageable) {
    return ResponseEntity.ok(clientService.getAllClients(pageable));
  }
}
