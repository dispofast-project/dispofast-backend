package com.dispocol.dispofast.modules.inventory.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productName", source = "stock.product.name")
    InventoryResponseDTO toInventoryResponseDTO(InventoryStock stock);

}
