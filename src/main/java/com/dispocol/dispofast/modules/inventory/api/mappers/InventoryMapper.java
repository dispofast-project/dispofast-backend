package com.dispocol.dispofast.modules.inventory.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

  @Mapping(target = "productId", source = "stock.product.id")
  @Mapping(target = "productName", source = "stock.product.name")
  @Mapping(target = "sku", source = "stock.product.sku")
  @Mapping(target = "category", source = "stock.product.category.name")
  @Mapping(target = "imageUrl", source = "stock.product.imageUrl")
  @Mapping(target = "taxFree", source = "stock.product.taxFree")
  InventoryResponseDTO toInventoryResponseDTO(InventoryStock stock);
}
