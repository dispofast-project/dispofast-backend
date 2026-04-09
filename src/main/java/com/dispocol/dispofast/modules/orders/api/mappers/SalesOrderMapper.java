package com.dispocol.dispofast.modules.orders.api.mappers;

import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.UpdateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SalesOrderMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "asesor", ignore = true)
  @Mapping(target = "state", ignore = true)
  @Mapping(target = "shipmentCity", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "quote", ignore = true)
  SalesOrder toEntity(CreateSalesOrderRequestDTO request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "asesor", ignore = true)
  @Mapping(target = "shipmentCity", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "quote", ignore = true)
  void updateEntityFromDTO(UpdateSalesOrderRequestDTO request, @MappingTarget SalesOrder order);

  @Mapping(target = "clientId", source = "client.id")
  @Mapping(
      target = "clientName",
      expression = "java(order.getClient() != null ? order.getClient().getDisplayName() : null)")
  @Mapping(target = "asesorUserId", source = "asesor.id")
  @Mapping(
      target = "asesorName",
      expression = "java(order.getAsesor() != null ? order.getAsesor().getFullName() : null)")
  @Mapping(target = "shipmentCityId", source = "shipmentCity.code")
  @Mapping(target = "shipmentCityName", source = "shipmentCity.name")
  @Mapping(target = "priceListId", source = "priceList.id")
  @Mapping(target = "quoteId", source = "quote.id")
  @Mapping(target = "items", ignore = true)
  SalesOrderResponseDTO toResponseDTO(SalesOrder order);

  List<SalesOrderResponseDTO> toResponseDTOList(List<SalesOrder> orders);
}
