package com.dispocol.dispofast.modules.inventory.application.interfaces;

import java.util.List;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;

public interface InventoryService {
    
    void addProductToInventory(Product product, int quantity);

    void reduceProductFromInventory(String productId, int quantity);
    
    List<ProductResponseDTO> getAllProductsInInventory();

}
