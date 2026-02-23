package com.dispocol.dispofast.modules.inventory.application.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.persistence.InventoryStockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public void addProductToInventory(Product product, int quantity) {
       try {

            String state = quantity >= 0 ? "IN_STOCK" : "OUT_OF_STOCK";

            inventoryStockRepository.findById(product.getId())
                .map(stock -> {
                    stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);
                    return inventoryStockRepository.save(stock);
                })
                .orElseGet(() -> {
                    InventoryStock newStock = new InventoryStock();
                    newStock.setProduct(product);
                    newStock.setQuantityAvailable(quantity);
                    newStock.setQuantityReserved(0);
                    newStock.setState(state);
                    return inventoryStockRepository.save(newStock);
                });
       } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto al inventario: " + e.getMessage(), e);
       }
    }

    @Override
    public void reduceProductFromInventory(String productId, int quantity) {
        try {
            inventoryStockRepository.findById(UUID.fromString(productId))
                .ifPresent(stock -> {
                    int newQuantity = stock.getQuantityAvailable() - quantity;
                    String newState = newQuantity > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
                    stock.setQuantityAvailable(newQuantity);
                    stock.setState(newState);
                    inventoryStockRepository.save(stock);
                });
        } catch (Exception e) {

            throw new RuntimeException("Error al reducir producto del inventario: " + e.getMessage(), e);

        }
    }

    @Override
    public List<ProductResponseDTO> getAllProductsInInventory() {
        List<InventoryStock> stocks = inventoryStockRepository.findAll();
        return stocks.stream()
            .map(stock -> {
                ProductResponseDTO dto = new ProductResponseDTO();
                dto.setId(stock.getProduct().getId());
                dto.setName(stock.getProduct().getName());
                dto.setSeoTitle(stock.getProduct().getSeoTitle());
                dto.setState(stock.getState());
                return dto;
            })
            .toList(); 
    }
    
}
