package com.dispocol.dispofast.modules.inventory.application.interfaces;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

  /** Called when a product is created to register its initial stock. */
  void addProductToInventory(Product product, int quantity);

  /**
   * Reserves stock when a sales order is created. Moves quantity from available to reserved. Throws
   * InsufficientStockException if not enough available stock.
   */
  void reserveStock(UUID productId, BigDecimal quantity);

  /**
   * Confirms stock when a sales order is delivered. Removes quantity from reserved (available was
   * already reduced at reserve time).
   */
  void confirmStock(UUID productId, BigDecimal quantity);

  /**
   * Releases reserved stock when a sales order is cancelled or deleted. Moves quantity back from
   * reserved to available.
   */
  void releaseStock(UUID productId, BigDecimal quantity);

  /**
   * Manual admin stock adjustment. Pass a positive value to increase or negative to decrease
   * available stock.
   */
  InventoryResponseDTO adjustStock(UUID productId, int delta);

  InventoryResponseDTO getStockByProductId(UUID productId);

  Page<InventoryResponseDTO> getInventoryStockForAllProducts(Pageable pageable);
}
