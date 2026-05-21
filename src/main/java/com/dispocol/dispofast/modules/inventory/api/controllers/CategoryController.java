package com.dispocol.dispofast.modules.inventory.api.controllers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CategoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
  public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }
}
