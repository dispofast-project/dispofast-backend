package com.dispocol.dispofast.modules.quotes.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.quotes.api.dtos.QuoteItemResponseDTO;
import com.dispocol.dispofast.modules.quotes.domain.QuoteItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class},
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface QuoteItemMapper {

  @Mapping(target = "product", source = "product")
  QuoteItemResponseDTO toResponseDTO(QuoteItem quoteItem);

  List<QuoteItemResponseDTO> toResponseDTOList(List<QuoteItem> items);
}
