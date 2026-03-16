package com.dispocol.dispofast.modules.inventory.application.interfaces;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.UpdateProductRequestDTO;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductResponseDTO createProduct(CreateProductRequestDTO request);

  ProductResponseDTO getProductById(UUID productId);

  Page<ProductResponseDTO> getAllProducts(Pageable pageable);

  ProductResponseDTO updateProduct(UUID productId, UpdateProductRequestDTO request);

  void deleteProduct(UUID productId);
}
