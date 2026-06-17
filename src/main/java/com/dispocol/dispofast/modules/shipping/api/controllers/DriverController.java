package com.dispocol.dispofast.modules.shipping.api.controllers;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateDriverDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.DriverResponseDTO;
import com.dispocol.dispofast.modules.shipping.application.interfaces.DriverService;
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
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

  private final DriverService driverService;

  @GetMapping
  public ResponseEntity<Page<DriverResponseDTO>> getAll(Pageable pageable) {
    return ResponseEntity.ok(driverService.getAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DriverResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(driverService.getById(id));
  }

  @PostMapping
  public ResponseEntity<DriverResponseDTO> create(@RequestBody CreateDriverDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DriverResponseDTO> update(
      @PathVariable UUID id, @RequestBody CreateDriverDTO dto) {
    return ResponseEntity.ok(driverService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    driverService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
