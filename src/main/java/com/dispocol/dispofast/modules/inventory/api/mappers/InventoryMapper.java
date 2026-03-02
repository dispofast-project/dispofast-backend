package com.dispocol.dispofast.modules.inventory.api.mappers;

import org.mapstruct.Mapper;

import com.dispocol.dispofast.modules.inventory.api.dtos.InventoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.InventoryStock;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponseDTO toInventoryResponseDTO(InventoryStock stock);
    
}
