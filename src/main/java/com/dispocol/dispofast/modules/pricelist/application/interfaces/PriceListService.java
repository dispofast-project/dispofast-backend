package com.dispocol.dispofast.modules.pricelist.application.interfaces;

import com.dispocol.dispofast.modules.pricelist.api.dtos.PriceListResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface PriceListService {

  List<PriceListResponseDTO> getAllPriceLists();

  void uploadPriceListItems(UUID priceListId, MultipartFile file);

  Optional<BigDecimal> resolveUnitPrice(UUID priceListId, UUID productId);
}
