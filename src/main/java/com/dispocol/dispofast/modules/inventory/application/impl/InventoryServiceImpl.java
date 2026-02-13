package com.dispocol.dispofast.modules.inventory.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotFoundException;
import com.dispocol.dispofast.modules.inventory.infra.persistence.InventoryStockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductServiceImpl productService;
    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public void addProductToInventory(String productId, int quantity) {
       try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new ProductNotFoundException("No se encontró el producto: " + productId);
            }

            inventoryStockRepository.findById(product.getId())
                .map(stock -> {
                    stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);
                    return inventoryStockRepository.save(stock);
                })
                .orElseGet(() -> {
                    InventoryStock newStock = new InventoryStock();
                    newStock.setProduct(product);
                    newStock.setQuantityAvailable(quantity);
                    return inventoryStockRepository.save(newStock);
                });
       } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto al inventario: " + e.getMessage(), e);
       }
    }

    @Override
    public void removeProductFromInventory(String productId, int quantity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeProductFromInventory'");
    }

    @Override
    public List<ProductResponseDTO> getAllProductsInInventory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProductsInInventory'");
    }
    
}
