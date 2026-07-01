package com.dispocol.dispofast.modules.shipping.api.controllers;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.CreateCarrierDTO;
import com.dispocol.dispofast.modules.shipping.application.interfaces.CarrierService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carriers")
@RequiredArgsConstructor
public class CarrierController {

  private final CarrierService carrierService;

  @GetMapping
  public ResponseEntity<Page<CarrierResponseDTO>> getAll(Pageable pageable) {
    return ResponseEntity.ok(carrierService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CarrierResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(carrierService.getById(id));
  }

  @PostMapping
  public ResponseEntity<CarrierResponseDTO> create(@RequestBody CreateCarrierDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(carrierService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CarrierResponseDTO> update(
      @PathVariable UUID id, @RequestBody CreateCarrierDTO dto) {
    return ResponseEntity.ok(carrierService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    carrierService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
