package com.dispocol.dispofast.modules.inventory.application.impl;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.InventoryMapper;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.domain.StockState;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.InsufficientStockException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotFoundException;
import com.dispocol.dispofast.modules.inventory.infra.persistence.InventoryStockRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private static final int LOW_STOCK_THRESHOLD = 10;

  private final InventoryStockRepository inventoryStockRepository;
  private final InventoryMapper inventoryMapper;

  @Override
  @Transactional
  public void addProductToInventory(Product product, int quantity) {
    inventoryStockRepository
        .findByProduct_Id(product.getId())
        .ifPresentOrElse(
            stock -> {
              stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);
              stock.setState(calculateState(stock.getQuantityAvailable()));
              inventoryStockRepository.save(stock);
            },
            () -> {
              InventoryStock newStock = new InventoryStock();
              newStock.setProduct(product);
              newStock.setQuantityAvailable(quantity);
              newStock.setQuantityReserved(0);
              newStock.setState(calculateState(quantity));
              inventoryStockRepository.save(newStock);
            });
  }

  @Override
  @Transactional
  public void reserveStock(UUID productId, BigDecimal quantity) {
    int qty = quantity.intValue();
    InventoryStock stock = findStockOrThrow(productId);

    if (stock.getQuantityAvailable() < qty) {
      throw new InsufficientStockException(
          "Stock insuficiente para el producto "
              + productId
              + ". Disponible: "
              + stock.getQuantityAvailable()
              + ", solicitado: "
              + qty);
    }

    stock.setQuantityAvailable(stock.getQuantityAvailable() - qty);
    stock.setQuantityReserved(stock.getQuantityReserved() + qty);
    stock.setState(calculateState(stock.getQuantityAvailable()));
    inventoryStockRepository.save(stock);
  }

  @Override
  @Transactional
  public void confirmStock(UUID productId, BigDecimal quantity) {
    int qty = quantity.intValue();
    InventoryStock stock = findStockOrThrow(productId);

    // available was already reduced at reserve time; just remove from reserved
    stock.setQuantityReserved(Math.max(0, stock.getQuantityReserved() - qty));
    inventoryStockRepository.save(stock);
  }

  @Override
  @Transactional
  public void releaseStock(UUID productId, BigDecimal quantity) {
    int qty = quantity.intValue();
    InventoryStock stock = findStockOrThrow(productId);

    stock.setQuantityAvailable(stock.getQuantityAvailable() + qty);
    stock.setQuantityReserved(Math.max(0, stock.getQuantityReserved() - qty));
    stock.setState(calculateState(stock.getQuantityAvailable()));
    inventoryStockRepository.save(stock);
  }

  @Override
  @Transactional
  public InventoryResponseDTO adjustStock(UUID productId, int delta) {
    InventoryStock stock = findStockOrThrow(productId);
    int newQty = stock.getQuantityAvailable() + delta;

    if (newQty < 0) {
      throw new InsufficientStockException(
          "El ajuste dejaría el stock en negativo. Disponible: "
              + stock.getQuantityAvailable()
              + ", ajuste: "
              + delta);
    }

    stock.setQuantityAvailable(newQty);
    stock.setState(calculateState(newQty));
    return inventoryMapper.toInventoryResponseDTO(inventoryStockRepository.save(stock));
  }

  @Override
  @Transactional(readOnly = true)
  public InventoryResponseDTO getStockByProductId(UUID productId) {
    return inventoryMapper.toInventoryResponseDTO(findStockOrThrow(productId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<InventoryResponseDTO> getInventoryStockForAllProducts(Pageable pageable) {
    return inventoryStockRepository.findAll(pageable).map(inventoryMapper::toInventoryResponseDTO);
  }

  private InventoryStock findStockOrThrow(UUID productId) {
    return inventoryStockRepository
        .findByProduct_Id(productId)
        .orElseThrow(
            () ->
                new ProductNotFoundException(
                    "No se encontró registro de inventario para el producto: " + productId));
  }

  private StockState calculateState(int quantityAvailable) {
    if (quantityAvailable <= 0) return StockState.OUT_OF_STOCK;
    if (quantityAvailable <= LOW_STOCK_THRESHOLD) return StockState.LOW_STOCK;
    return StockState.IN_STOCK;
  }
}
