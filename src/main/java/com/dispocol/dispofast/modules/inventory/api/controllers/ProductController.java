package com.dispocol.dispofast.modules.inventory.api.controllers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.ProductService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping("/create-product")
  public ResponseEntity<ProductResponseDTO> createProduct(
      @RequestBody CreateProductRequestDTO request) {

    ProductResponseDTO response = productService.createProduct(request);
    return ResponseEntity.ok(response);
  }
}
