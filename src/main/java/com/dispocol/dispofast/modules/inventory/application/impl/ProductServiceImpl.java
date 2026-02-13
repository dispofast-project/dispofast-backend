package com.dispocol.dispofast.modules.inventory.application.impl;

import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.application.interfaces.ProductService;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductAlreadyExistsException;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    @Override
    public ProductResponseDTO createProduct(CreateProductRequestDTO request) {
        
        Product product = productMapper.fromCreateProductRequestDTO(request);

        if(productRepository.existsBySeoTitle(request.getSeoTitle())){
            throw new ProductAlreadyExistsException("El producto con el SEO Title '" + request.getSeoTitle() + "' ya existe.");
        }

        Product savedProduct = productRepository.save(product);

        inventoryService.addProductToInventory(savedProduct, request.getInitialStock());

        return productMapper.toProductResponseDTO(savedProduct);

    }

    @Override
    public Product getProductById(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductById'");
    }

    @Override
    public void deleteProduct(String productId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProduct'");
    }

    @Override
    public ProductResponseDTO updateProduct(String productId, CreateProductRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProduct'");
    }
    
}
