package com.dispocol.dispofast.modules.inventory.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

  @Mapping(target = "productId",        source = "stock.product.id")
  @Mapping(target = "productName",      source = "stock.product.name")
  @Mapping(target = "productReference", source = "stock.product.reference")
  @Mapping(target = "taxFree",          source = "stock.product.taxFree")
  InventoryResponseDTO toInventoryResponseDTO(InventoryStock stock);
}
