package com.dispocol.dispofast.modules.inventory.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.dtos.CategoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryResponseDTO toCategoryResponseDTO(Category category);
}
