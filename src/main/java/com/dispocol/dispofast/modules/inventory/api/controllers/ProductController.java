package com.dispocol.dispofast.modules.inventory.api.controllers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.UpdateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.ProductService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping
  @PreAuthorize("hasAuthority('INVENTORY_CREATE')")
  public ResponseEntity<ProductResponseDTO> createProduct(
      @Valid @RequestBody CreateProductRequestDTO request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
  public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(Pageable pageable) {
    return ResponseEntity.ok(productService.getAllProducts(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
  public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
  public ResponseEntity<ProductResponseDTO> updateProduct(
      @PathVariable UUID id, @Valid @RequestBody UpdateProductRequestDTO request) {
    return ResponseEntity.ok(productService.updateProduct(id, request));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('INVENTORY_DELETE')")
  public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
