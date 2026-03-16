package com.dispocol.dispofast.modules.inventory.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CreateProductRequestDTO;
import com.dispocol.dispofast.modules.inventory.api.dtos.ProductResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "name", expression = "java(dto.getName().trim())")
  @Mapping(target = "shortDescription", expression = "java(dto.getShortDescription().trim())")
  @Mapping(target = "longDescription", expression = "java(dto.getLongDescription().trim())")
  @Mapping(target = "imageUrl", expression = "java(dto.getImageUrl().trim())")
  @Mapping(target = "sku", expression = "java(dto.getSku().trim().toUpperCase())")
  @Mapping(target = "reference", expression = "java(dto.getReference().trim().toUpperCase())")
  @Mapping(target = "size", expression = "java(dto.getSize().trim())")
  @Mapping(target = "seoTitle", expression = "java(dto.getSeoTitle().trim())")
  @Mapping(target = "seoDescription", expression = "java(dto.getSeoDescription().trim())")
  @Mapping(target = "seoKeywords", expression = "java(dto.getSeoKeywords().trim())")
  @Mapping(target = "state", source = "state")
  Product fromCreateProductRequestDTO(CreateProductRequestDTO dto);

  @Mapping(
      target = "categoryName",
      expression =
          "java(product != null && product.getCategory() != null ? product.getCategory().getName() : null)")
  ProductResponseDTO toProductResponseDTO(Product product);
}
