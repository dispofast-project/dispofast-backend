package com.dispocol.dispofast.unit.inventory.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapperImpl;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.domain.ProductState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {ProductMapperImpl.class})
@DisplayName("ProductMapper - Unit Test")
public class ProductMapperTest {

  @Autowired private ProductMapper productMapper;

  private Product testProduct;

  @BeforeEach
  void setUp() {
    testProduct = new Product();
    testProduct.setId(java.util.UUID.randomUUID());
    testProduct.setName("Test Product");
    testProduct.setShortDescription("This is a test product.");
    testProduct.setLongDescription("This is a long description for the test product.");
    testProduct.setSeoTitle("test-product");
  }

  @Test
  @DisplayName("should map Product to ProductResponseDTO correctly")
  void shouldMapProductToProductResponseDTO() {
    ProductResponseDTO productResponseDTO = productMapper.toProductResponseDTO(testProduct);

    assert productResponseDTO.getName().equals(testProduct.getName());
    assert productResponseDTO.getShortDescription().equals(testProduct.getShortDescription());
    assert productResponseDTO.getLongDescription().equals(testProduct.getLongDescription());
    assert productResponseDTO.getSeoTitle().equals(testProduct.getSeoTitle());
  }

  @Test
  @DisplayName("should map CreateProductRequestDTO to Product correctly")
  void shouldMapCreateProductRequestDTOToProduct() {
    CreateProductRequestDTO createProductRequestDTO = new CreateProductRequestDTO();
    createProductRequestDTO.setName("New Product");
    createProductRequestDTO.setShortDescription("Short description of new product.");
    createProductRequestDTO.setLongDescription("Long description of new product.");
    createProductRequestDTO.setSeoTitle("new-product");
    createProductRequestDTO.setInitialStock(50);
    createProductRequestDTO.setImageUrl("image.png");
    createProductRequestDTO.setSku("NP001");
    createProductRequestDTO.setReference("REF001");
    createProductRequestDTO.setSize("M");
    createProductRequestDTO.setState(ProductState.ACTIVE);
    createProductRequestDTO.setSeoDescription("SEO description for test");
    createProductRequestDTO.setSeoKeywords("test, product, seo");

    Product product = productMapper.fromCreateProductRequestDTO(createProductRequestDTO);

    assert product.getName().equals(createProductRequestDTO.getName());
    assert product.getShortDescription().equals(createProductRequestDTO.getShortDescription());
    assert product.getLongDescription().equals(createProductRequestDTO.getLongDescription());
    assert product.getSeoTitle().equals(createProductRequestDTO.getSeoTitle());
    assert product.getImageUrl().equals(createProductRequestDTO.getImageUrl());
    assert product.getSku().equals(createProductRequestDTO.getSku());
    assert product.getReference().equals(createProductRequestDTO.getReference());
    assert product.getSize().equals(createProductRequestDTO.getSize());
    assert product.getState().equals(createProductRequestDTO.getState());
    assert product.getSeoDescription().equals(createProductRequestDTO.getSeoDescription());
    assert product.getSeoKeywords().equals(createProductRequestDTO.getSeoKeywords());
  }
}
