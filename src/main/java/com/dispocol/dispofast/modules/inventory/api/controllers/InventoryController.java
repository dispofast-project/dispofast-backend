package com.dispocol.dispofast.modules.inventory.api.controllers;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/all-products")
  public ResponseEntity<Page<InventoryResponseDTO>> getAllProducts(Pageable pageable) {
    Page<InventoryResponseDTO> response = inventoryService.getInventoryStockForAllProducts(pageable);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/decrease/{id}")
  public ResponseEntity<String> decreaseProductInInventory(
      @PathVariable String id, @RequestBody int quantity) {
    try {
      inventoryService.reduceProductFromInventory(id, quantity);
      return ResponseEntity.ok("Producto disminuido correctamente");
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body("Error al disminuir cantidad del producto: " + e.getMessage());
    }
  }

  @PutMapping("/increase/{id}")
  public ResponseEntity<String> increaseProductInInventory(
      @PathVariable String id, @RequestBody int quantity) {
    try {
      String response = inventoryService.addProductQuantity(id, quantity);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body("Error al aumentar cantidad del producto: " + e.getMessage());
    }
  }
}
