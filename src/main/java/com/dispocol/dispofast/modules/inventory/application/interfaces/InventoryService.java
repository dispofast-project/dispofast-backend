package com.dispocol.dispofast.modules.inventory.application.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;

public interface InventoryService {

  void addProductToInventory(Product product, int quantity);

  void reduceProductFromInventory(String productId, int quantity);
  
  List<ProductResponseDTO> getAllProductsInInventory();

  Page<InventoryResponseDTO> getInventoryStockForAllProducts(Pageable pageable);

  String addProductQuantity(String productId, int quantity);

}
