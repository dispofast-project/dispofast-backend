package com.dispocol.dispofast.modules.shipping.api.controllers;

import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.UpdateShipmentDTO;
import com.dispocol.dispofast.modules.shipping.application.interfaces.ShipmentService;
import com.dispocol.dispofast.modules.shipping.domain.ShipmentState;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

  private final ShipmentService shipmentService;

  @GetMapping
  public ResponseEntity<Page<ShipmentResponseDTO>> getAll(
      @RequestParam(required = false) ShipmentState state,
      @RequestParam(required = false) String clientName,
      @RequestParam(required = false) String asesorName,
      @RequestParam(required = false) LocalDate dateFrom,
      @RequestParam(required = false) LocalDate dateTo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return ResponseEntity.ok(
        shipmentService.getAll(state, clientName, asesorName, dateFrom, dateTo, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShipmentResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(shipmentService.getById(id));
  }

  @GetMapping("/invoice/{invoiceId}")
  public ResponseEntity<ShipmentResponseDTO> getByInvoiceId(@PathVariable UUID invoiceId) {
    return ResponseEntity.ok(shipmentService.getByInvoiceId(invoiceId));
  }

  @GetMapping("/carrier/{carrierId}")
  public ResponseEntity<List<ShipmentResponseDTO>> getByCarrierId(@PathVariable UUID carrierId) {
    return ResponseEntity.ok(shipmentService.getByCarrierId(carrierId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ShipmentResponseDTO> update(
      @PathVariable UUID id, @RequestBody UpdateShipmentDTO dto) {
    return ResponseEntity.ok(shipmentService.update(id, dto));
  }

  @PutMapping("/{id}/state")
  public ResponseEntity<ShipmentResponseDTO> updateState(
      @PathVariable UUID id, @RequestParam ShipmentState state) {
    return ResponseEntity.ok(shipmentService.updateState(id, state));
  }
}
