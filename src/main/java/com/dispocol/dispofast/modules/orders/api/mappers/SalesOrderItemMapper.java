package com.dispocol.dispofast.modules.orders.api.mappers;

import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderItemDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderItemResponseDTO;
import com.dispocol.dispofast.modules.orders.domain.SalesOrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesOrderItemMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "unitPrice", ignore = true)
  @Mapping(target = "lineTotal", ignore = true)
  SalesOrderItem toEntity(CreateSalesOrderItemDTO dto);

  @Mapping(target = "productId",        source = "product.id")
  @Mapping(target = "productReference", source = "product.reference")
  @Mapping(target = "taxFree",          source = "product.taxFree")
  @Mapping(
      target = "productName",
      expression = "java(item.getProduct() != null ? item.getProduct().getName() : null)")
  SalesOrderItemResponseDTO toResponseDTO(SalesOrderItem item);

  List<SalesOrderItemResponseDTO> toResponseDTOList(List<SalesOrderItem> items);
}
