package com.dispocol.dispofast.modules.purchases.api.mappers;

import com.dispocol.dispofast.modules.inventory.api.mappers.ProductMapper;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderItemResponseDTO;
import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class},
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PurchaseOrderItemMapper {

  @Mapping(target = "product", source = "product")
  PurchaseOrderItemResponseDTO toResponseDTO(PurchaseOrderItem item);

  List<PurchaseOrderItemResponseDTO> toResponseDTOList(List<PurchaseOrderItem> items);
}
