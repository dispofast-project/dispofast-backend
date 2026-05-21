package com.dispocol.dispofast.modules.inventory.application.interfaces;

import com.dispocol.dispofast.modules.inventory.api.dtos.CategoryResponseDTO;
import java.util.List;

public interface CategoryService {

  List<CategoryResponseDTO> getAllCategories();
}
