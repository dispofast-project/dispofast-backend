package com.dispocol.dispofast.modules.customers.api.controllers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientTypeDTO;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client-types")
@RequiredArgsConstructor
public class ClientTypeController {

  private final ClientTypeRepository clientTypeRepository;

  @GetMapping
  public ResponseEntity<List<ClientTypeDTO>> getAllClientTypes() {
    List<ClientTypeDTO> clientTypes =
        clientTypeRepository.findAll().stream()
            .map(ct -> ClientTypeDTO.builder().id(ct.getId()).name(ct.getName()).build())
            .toList();
    return ResponseEntity.ok(clientTypes);
  }
}
