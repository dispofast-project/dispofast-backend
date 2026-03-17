package com.dispocol.dispofast.modules.inventory.application.impl;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.UpdateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.application.interfaces.ProductService;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductAlreadyExistsException;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotFoundException;
import com.dispocol.dispofast.modules.inventory.infra.persistence.CategoryRepository;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;
  private final InventoryService inventoryService;

  @Override
  @Transactional
  public ProductResponseDTO createProduct(CreateProductRequestDTO request) {
    if (productRepository.existsBySeoTitle(request.getSeoTitle())) {
      throw new ProductAlreadyExistsException(
          "El producto con el SEO Title '" + request.getSeoTitle() + "' ya existe.");
    }

    Product product = productMapper.fromCreateProductRequestDTO(request);
    product.setCategory(categoryRepository.getReferenceById(request.getCategoryId()));

    Product savedProduct = productRepository.save(product);
    inventoryService.addProductToInventory(savedProduct, request.getInitialStock());

    return productMapper.toProductResponseDTO(savedProduct);
  }

  @Override
  @Transactional(readOnly = true)
  public ProductResponseDTO getProductById(UUID productId) {
    return productMapper.toProductResponseDTO(findProductOrThrow(productId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable).map(productMapper::toProductResponseDTO);
  }

  @Override
  @Transactional
  public ProductResponseDTO updateProduct(UUID productId, UpdateProductRequestDTO request) {
    Product product = findProductOrThrow(productId);

    product.setName(request.getName().trim());
    product.setShortDescription(request.getShortDescription().trim());
    product.setLongDescription(request.getLongDescription().trim());
    product.setImageUrl(request.getImageUrl().trim());
    product.setTaxFree(request.getTaxFree());
    product.setSku(request.getSku().trim().toUpperCase());
    product.setReference(request.getReference().trim().toUpperCase());
    product.setSize(request.getSize().trim());
    product.setSeoTitle(request.getSeoTitle().trim());
    product.setSeoDescription(request.getSeoDescription().trim());
    product.setSeoKeywords(request.getSeoKeywords().trim());
    product.setState(request.getState());

    if (!product.getCategory().getId().equals(request.getCategoryId())) {
      product.setCategory(categoryRepository.getReferenceById(request.getCategoryId()));
    }

    return productMapper.toProductResponseDTO(productRepository.save(product));
  }

  @Override
  @Transactional
  public void deleteProduct(UUID productId) {
    if (!productRepository.existsById(productId)) {
      throw new ProductNotFoundException("Producto no encontrado con ID: " + productId);
    }
    productRepository.deleteById(productId);
  }

  private Product findProductOrThrow(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> new ProductNotFoundException("Producto no encontrado con ID: " + productId));
  }
}
