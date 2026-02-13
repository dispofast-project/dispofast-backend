package com.dispocol.dispofast.modules.inventory.application.interfaces;

import java.util.List;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;

public interface InventoryService {
    
    void addProductToInventory(String productId, int quantity);

    void removeProductFromInventory(String productId, int quantity);
    
    List<ProductResponseDTO> getAllProductsInInventory();

}
