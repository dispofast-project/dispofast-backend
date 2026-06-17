package com.dispocol.dispofast.modules.shipping.api.controllers;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateVehicleDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.VehicleResponseDTO;
import com.dispocol.dispofast.modules.shipping.application.interfaces.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

  private final VehicleService vehicleService;

  @GetMapping
  public ResponseEntity<Page<VehicleResponseDTO>> getAll(Pageable pageable) {
    return ResponseEntity.ok(vehicleService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<VehicleResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(vehicleService.getById(id));
  }

  @PostMapping
  public ResponseEntity<VehicleResponseDTO> create(@RequestBody CreateVehicleDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<VehicleResponseDTO> update(
      @PathVariable UUID id, @RequestBody CreateVehicleDTO dto) {
    return ResponseEntity.ok(vehicleService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    vehicleService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
