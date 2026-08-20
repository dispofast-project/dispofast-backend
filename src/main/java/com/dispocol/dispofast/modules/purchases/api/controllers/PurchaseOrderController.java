package com.dispocol.dispofast.modules.purchases.api.controllers;

import com.dispocol.dispofast.modules.purchases.api.dtos.CreatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderPreviewResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.application.interfaces.PurchaseOrderService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;

  @PostMapping
  @PreAuthorize("hasAuthority('PURCHASES_CREATE')")
  public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(
      @RequestBody CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO) {
    return new ResponseEntity<>(
        purchaseOrderService.createPurchaseOrder(createPurchaseOrderRequestDTO),
        HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASES_VIEW')")
  public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrderById(@PathVariable UUID id) {
    return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PURCHASES_EDIT')")
  public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrder(
      @PathVariable UUID id,
      @Valid @RequestBody UpdatePurchaseOrderRequestDTO updatePurchaseOrderRequestDTO) {
    return ResponseEntity.ok(
        purchaseOrderService.updatePurchaseOrder(id, updatePurchaseOrderRequestDTO));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('PURCHASES_VIEW')")
  public ResponseEntity<Page<PurchaseOrderPreviewResponseDTO>> getAllPurchaseOrders(
      @RequestParam(required = false) String text,
      @RequestParam(required = false) String key,
      Pageable pageable) {
    return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders(text, key, pageable));
  }
}
