package com.dispocol.dispofast.modules.inventory.application.impl;

import com.dispocol.dispofast.modules.inventory.api.dtos.CategoryResponseDTO;
import com.dispocol.dispofast.modules.inventory.api.mappers.CategoryMapper;
import com.dispocol.dispofast.modules.inventory.application.interfaces.CategoryService;
import com.dispocol.dispofast.modules.inventory.infra.persistence.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponseDTO> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::toCategoryResponseDTO)
        .toList();
  }
}
