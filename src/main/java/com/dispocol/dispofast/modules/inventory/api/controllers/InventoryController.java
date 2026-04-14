package com.dispocol.dispofast.modules.inventory.api.controllers;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping
  @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
  public ResponseEntity<Page<InventoryResponseDTO>> getAllInventory(Pageable pageable) {
    return ResponseEntity.ok(inventoryService.getInventoryStockForAllProducts(pageable));
  }

  @GetMapping("/product/{productId}")
  @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
  public ResponseEntity<InventoryResponseDTO> getStockByProduct(@PathVariable UUID productId) {
    return ResponseEntity.ok(inventoryService.getStockByProductId(productId));
  }

  @PutMapping("/product/{productId}/adjust")
  @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
  public ResponseEntity<InventoryResponseDTO> adjustStock(
      @PathVariable UUID productId, @RequestBody int delta) {
    return ResponseEntity.ok(inventoryService.adjustStock(productId, delta));
  }
}
