package com.dispocol.dispofast.modules.inventory.application.impl;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.InventoryMapper;
import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.persistence.InventoryStockRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryStockRepository inventoryStockRepository;
  private final InventoryMapper inventoryMapper;
  private final ProductMapper productMapper;

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
  public void reduceProductFromInventory(String productId, int quantity) {
    try {
      inventoryStockRepository
          .findById(UUID.fromString(productId))
          .ifPresent(
              stock -> {
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
        .map(
            stock -> {
              Product product = stock.getProduct();
              return productMapper.toProductResponseDTO(product);
            })
        .toList();
  }

  @Override
  public Page<InventoryResponseDTO> getInventoryStockForAllProducts(Pageable pageable) {
    Page<InventoryStock> stocks = inventoryStockRepository.findAll(pageable);
    return stocks.map(inventoryMapper::toInventoryResponseDTO);
  }

  @Override
  public String addProductQuantity(String productId, int quantity) {
    try {
      inventoryStockRepository
          .findById(UUID.fromString(productId))
          .ifPresent(
              stock -> {
                int newQuantity = stock.getQuantityAvailable() + quantity;
                String newState = newQuantity > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
                stock.setQuantityAvailable(newQuantity);
                stock.setState(newState);
                inventoryStockRepository.save(stock);
              });
      return "Producto actualizado correctamente";
    } catch (Exception e) {
      throw new RuntimeException("Error al actualizar cantidad del producto: " + e.getMessage(), e);
    }
  }
}
