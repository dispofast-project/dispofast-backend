package com.dispocol.dispofast.modules.inventory.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(
        target = "name", 
        expression = "java(dto != null ? dto.getName().trim() : null)"
    )
    @Mapping(
        target = "shortDescription", 
        expression = "java(dto != null ? dto.getShortDescription().trim() : null)"
    )
    @Mapping(
        target = "longDescription", 
        expression = "java(dto != null ? dto.getLongDescription().trim() : null)"
    )
    @Mapping(
        target = "imageUrl", 
        expression = "java(dto != null ? dto.getImageUrl().trim() : null)"
    )
    @Mapping(
        target = "sku", 
        expression = "java(dto != null ? dto.getSku().trim().toUpperCase() : null)"
    )
    @Mapping(
        target = "reference", 
        expression = "java(dto != null ? dto.getReference().trim().toUpperCase() : null)"
    )
    @Mapping(
        target = "size", 
        expression = "java(dto != null ? dto.getSize().trim() : null)"
    )
    @Mapping(
        target = "seoTitle", 
        expression = "java(dto != null ? dto.getSeoTitle().trim() : null)"
    )
    @Mapping(
        target = "seoDescription", 
        expression = "java(dto != null ? dto.getSeoDescription().trim() : null)"
    )
    @Mapping(
        target = "seoKeywords", 
        expression = "java(dto != null ? dto.getSeoKeywords().trim() : null)"
    )
    @Mapping(
        target = "state", 
        expression = "java(dto != null ? dto.getState().trim() : null)"
    )
    Product fromCreateProductRequestDTO(CreateProductRequestDTO dto);

    @Mapping(
        target = "categoryName", 
        expression = "java(product != null && product.getCategory() != null ? product.getCategory().getName() : null)"
    )
    ProductResponseDTO toProductResponseDTO(Product product);

    
}
