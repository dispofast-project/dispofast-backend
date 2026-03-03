package com.dispocol.dispofast.unit.inventory.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.InventoryMapper;
import com.dispocol.dispofast.modules.inventory.api.mappers.InventoryMapperImpl;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {InventoryMapperImpl.class})
@DisplayName("InventoryMapper - Unit Test")
public class InventoryMapperTest {

  @Autowired private InventoryMapper inventoryMapper;

  private InventoryStock testStock;
  private final Product testProduct = new Product();
  private final String productName = "Test Product";

  @BeforeEach
  void setUp() {
    testStock = new InventoryStock();
    testStock.setQuantityAvailable(100);
    testStock.setQuantityReserved(20);
    testStock.setState("IN_STOCK");

    testProduct.setId(java.util.UUID.randomUUID());
    testProduct.setName(productName);

    testStock.setProduct(testProduct);
  }

  @Test
  @DisplayName("should map InventoryStock to InventoryResponseDTO correctly")
  void shouldMapInventoryStockToInventoryResponseDTO() {
    InventoryResponseDTO inventoryResponseDTO = inventoryMapper.toInventoryResponseDTO(testStock);

    assert inventoryResponseDTO.getProductName().equals(testProduct.getName());
    assert inventoryResponseDTO.getQuantityAvailable() == testStock.getQuantityAvailable();
    assert inventoryResponseDTO.getQuantityReserved() == testStock.getQuantityReserved();
    assert inventoryResponseDTO.getState().equals(testStock.getState());
  }

  @Test
  @DisplayName("should handle null product in InventoryStock mapping")
  void shouldHandleNullProductInInventoryStockMapping() {
    testStock.setProduct(null);
    InventoryResponseDTO inventoryResponseDTO = inventoryMapper.toInventoryResponseDTO(testStock);

    assert inventoryResponseDTO.getProductName() == null;
    assert inventoryResponseDTO.getQuantityAvailable() == testStock.getQuantityAvailable();
    assert inventoryResponseDTO.getQuantityReserved() == testStock.getQuantityReserved();
    assert inventoryResponseDTO.getState().equals(testStock.getState());
  }
}
