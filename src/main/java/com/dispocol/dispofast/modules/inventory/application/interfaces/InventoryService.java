package com.dispocol.dispofast.modules.inventory.application.interfaces;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import java.util.List;

public interface InventoryService {

  void addProductToInventory(Product product, int quantity);

  void removeProductFromInventory(String productId, int quantity);

  List<ProductResponseDTO> getAllProductsInInventory();
}
