package com.dispocol.dispofast.modules.purchases.api.controllers;

import com.dispocol.dispofast.modules.purchases.api.dtos.AddPurchaseOrderItemRequestDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderItemResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderItemRequestDTO;
import com.dispocol.dispofast.modules.purchases.application.interfaces.PurchaseOrderItemService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-orders/{purchaseOrderId}/items")
@RequiredArgsConstructor
public class PurchaseOrderItemController {

  private final PurchaseOrderItemService purchaseOrderItemService;

  @PostMapping
  @PreAuthorize("hasAuthority('PURCHASES_EDIT')")
  public ResponseEntity<PurchaseOrderItemResponseDTO> addItem(
      @PathVariable UUID purchaseOrderId, @Valid @RequestBody AddPurchaseOrderItemRequestDTO dto) {
    return new ResponseEntity<>(
        purchaseOrderItemService.addItem(purchaseOrderId, dto), HttpStatus.CREATED);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('PURCHASES_VIEW')")
  public ResponseEntity<List<PurchaseOrderItemResponseDTO>> getItems(
      @PathVariable UUID purchaseOrderId) {
    return ResponseEntity.ok(purchaseOrderItemService.getItems(purchaseOrderId));
  }

  @PutMapping("/{itemId}")
  @PreAuthorize("hasAuthority('PURCHASES_EDIT')")
  public ResponseEntity<PurchaseOrderItemResponseDTO> updateItem(
      @PathVariable UUID purchaseOrderId,
      @PathVariable UUID itemId,
      @RequestBody UpdatePurchaseOrderItemRequestDTO dto) {
    return ResponseEntity.ok(purchaseOrderItemService.updateItem(purchaseOrderId, itemId, dto));
  }

  @DeleteMapping("/{itemId}")
  @PreAuthorize("hasAuthority('PURCHASES_EDIT')")
  public ResponseEntity<Void> removeItem(
      @PathVariable UUID purchaseOrderId, @PathVariable UUID itemId) {
    purchaseOrderItemService.removeItem(purchaseOrderId, itemId);
    return ResponseEntity.noContent().build();
  }
}
