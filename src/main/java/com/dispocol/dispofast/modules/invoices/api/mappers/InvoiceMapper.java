package com.dispocol.dispofast.modules.invoices.api.mappers;

import com.dispocol.dispofast.modules.invoices.api.dtos.InvoiceResponseDTO;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

  @Mapping(target = "salesOrderId", source = "salesOrder.id")
  @Mapping(
      target = "orderNumber",
      expression =
          "java(invoice.getSalesOrder() != null ? invoice.getSalesOrder().getOrderNumber() : null)")
  @Mapping(target = "clientId", source = "client.id")
  @Mapping(
      target = "clientName",
      expression =
          "java(invoice.getClient() != null ? invoice.getClient().getDisplayName() : null)")
  InvoiceResponseDTO toResponseDTO(Invoice invoice);
}
