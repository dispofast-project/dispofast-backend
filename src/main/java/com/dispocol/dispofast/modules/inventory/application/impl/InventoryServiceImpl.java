package com.dispocol.dispofast.modules.inventory.application.impl;

import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.persistence.InventoryStockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryStockRepository inventoryStockRepository;

  @Override
  public void addProductToInventory(Product product, int quantity) {
    try {

      String state = quantity >= 0 ? "IN_STOCK" : "OUT_OF_STOCK";

      inventoryStockRepository
          .findById(product.getId())
          .map(
              stock -> {
                stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);
                return inventoryStockRepository.save(stock);
              })
          .orElseGet(
              () -> {
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
