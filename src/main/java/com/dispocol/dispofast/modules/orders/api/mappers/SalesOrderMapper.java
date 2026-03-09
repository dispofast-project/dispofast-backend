package com.dispocol.dispofast.modules.orders.api.mappers;

import com.dispocol.dispofast.modules.orders.api.dtos.AttachInvoiceRequestDTO;
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

  // All entity references are ignored here — resolved in the service via getReferenceById()
  // to avoid Hibernate 6 TransientObjectException with unsaved proxy instances.
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "asesor", ignore = true)
  @Mapping(target = "state", ignore = true)
  @Mapping(target = "shipmentCity", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "quote", ignore = true)
  @Mapping(target = "invoiceNumber", ignore = true)
  @Mapping(target = "invoiceUrl", ignore = true)
  SalesOrder toEntity(CreateSalesOrderRequestDTO request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "asesor", ignore = true)
  @Mapping(target = "shipmentCity", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "quote", ignore = true)
  @Mapping(target = "invoiceNumber", ignore = true)
  @Mapping(target = "invoiceUrl", ignore = true)
  void updateEntityFromDTO(UpdateSalesOrderRequestDTO request, @MappingTarget SalesOrder order);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderNumber", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "asesor", ignore = true)
  @Mapping(target = "state", ignore = true)
  @Mapping(target = "orderDate", ignore = true)
  @Mapping(target = "shipmentCity", ignore = true)
  @Mapping(target = "shipmentAddress", ignore = true)
  @Mapping(target = "zone", ignore = true)
  @Mapping(target = "totalValue", ignore = true)
  @Mapping(target = "priceList", ignore = true)
  @Mapping(target = "quote", ignore = true)
  void applyInvoice(AttachInvoiceRequestDTO request, @MappingTarget SalesOrder order);

  @Mapping(target = "accountId", source = "account.id")
  @Mapping(
      target = "accountName",
      expression = "java(order.getAccount() != null ? order.getAccount().getName() : null)")
  @Mapping(target = "asesorUserId", source = "asesor.id")
  @Mapping(
      target = "asesorName",
      expression = "java(order.getAsesor() != null ? order.getAsesor().getFullName() : null)")
  @Mapping(target = "shipmentCityId", source = "shipmentCity.cityCode")
  @Mapping(target = "shipmentCityName", source = "shipmentCity.cityName")
  @Mapping(target = "priceListId", source = "priceList.id")
  @Mapping(target = "quoteId", source = "quote.id")
  @Mapping(target = "items", ignore = true)
  SalesOrderResponseDTO toResponseDTO(SalesOrder order);

  List<SalesOrderResponseDTO> toResponseDTOList(List<SalesOrder> orders);
}
