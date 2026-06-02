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
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAssetType;
import com.dispocol.dispofast.shared.MediaAsset.persistence.MediaAssetRepository;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private static final String PRODUCT_IMAGES_BUCKET = "dispofast-product-images";

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;
  private final InventoryService inventoryService;
  private final S3Service s3Service;
  private final MediaAssetRepository mediaAssetRepository;

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
    if (request.getImageUrl() != null) {
      product.setImageUrl(request.getImageUrl().trim());
    }
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

  @Override
  @Transactional
  public ProductResponseDTO uploadProductImage(UUID productId, MultipartFile file) {
    Product product = findProductOrThrow(productId);

    String storageKey = "products/" + productId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
    try {
      s3Service.uploadFile(
          PRODUCT_IMAGES_BUCKET, storageKey, file.getInputStream(), file.getContentType(), file.getSize());
    } catch (IOException e) {
      throw new IllegalArgumentException("No fue posible subir la imagen. Por favor intenta de nuevo.", e);
    }

    MediaAsset asset = new MediaAsset();
    asset.setFilename(file.getOriginalFilename());
    asset.setStoragePath(storageKey);
    asset.setMimeType(file.getContentType());
    asset.setFileSize(file.getSize());
    asset.setType(MediaAssetType.PRODUCT_IMAGE);
    mediaAssetRepository.save(asset);

    product.setImageUrl(s3Service.getFileUrl(PRODUCT_IMAGES_BUCKET, storageKey));
    return productMapper.toProductResponseDTO(productRepository.save(product));
  }

  private Product findProductOrThrow(UUID productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(
            () -> new ProductNotFoundException("Producto no encontrado con ID: " + productId));
  }
}
