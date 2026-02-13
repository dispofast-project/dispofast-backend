package com.dispocol.dispofast.modules.inventory.application.interfaces;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;

public interface ProductService {
    
    /**
     * Creates a new product in the inventory.
     * 
     * @param request The details of the product to be created.
     * @return The created product's details.
     */
    ProductResponseDTO createProduct(CreateProductRequestDTO request);

    /**
     * Retrieves a product by its unique identifier.
     * 
     * @param productId The unique identifier of the product.
     * @return The details of the requested product.
     */
    Product getProductById(String productId);

    /**
     * Deletes a product by its unique identifier.
     * 
     * @param productId The unique identifier of the product to be deleted.
     */
    void deleteProduct(String productId);

    /**
     * Updates an existing product in the inventory.
     * 
     * @param productId The unique identifier of the product to be updated.
     * @param request The new details of the product.
     * @return The updated product's details.
     */
    ProductResponseDTO updateProduct(String productId, CreateProductRequestDTO request);
}
