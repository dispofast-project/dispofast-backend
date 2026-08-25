package com.dispocol.dispofast.modules.purchases.api.mappers;

import com.dispocol.dispofast.modules.customers.api.mappers.ClientMapper;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderPreviewResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.PurchaseOrderResponseDTO;
import com.dispocol.dispofast.modules.purchases.api.dtos.UpdatePurchaseOrderRequestDTO;
import com.dispocol.dispofast.modules.purchases.domain.PurchaseOrder;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {ClientMapper.class, PurchaseOrderItemMapper.class},
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PurchaseOrderMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "buyer", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "number", ignore = true)
  @Mapping(target = "supplier", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  // Campos calculados — ignorados en el update, el servicio los recalcula
  @Mapping(target = "subtotalAmount", ignore = true)
  @Mapping(target = "commercialDiscountAmount", ignore = true)
  @Mapping(target = "otherDiscountsAmount", ignore = true)
  @Mapping(target = "ivaRate", ignore = true)
  @Mapping(target = "ivaAmount", ignore = true)
  @Mapping(target = "retefuenteRate", ignore = true)
  @Mapping(target = "retefuenteAmount", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "items", ignore = true)
  void updateEntityFromDTO(
      UpdatePurchaseOrderRequestDTO updatePurchaseOrderRequestDTO,
      @MappingTarget PurchaseOrder purchaseOrder);

  @Mapping(target = "supplier", source = "supplier")
  @Mapping(target = "items", source = "items")
  @Mapping(
      target = "buyerId",
      expression =
          "java(purchaseOrder.getBuyer() != null ? purchaseOrder.getBuyer().getId() : null)")
  @Mapping(
      target = "buyerName",
      expression =
          "java(purchaseOrder.getBuyer() != null ? purchaseOrder.getBuyer().getFullName() : null)")
  PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder purchaseOrder);

  @Mapping(target = "supplierName", expression = "java(clientToName(order.getSupplier()))")
  @Mapping(target = "total", source = "totalAmount")
  PurchaseOrderPreviewResponseDTO toPreviewResponseDTO(PurchaseOrder order);

  default String clientToName(Client client) {
    if (client == null) return null;
    if (client instanceof Individual ind) {
      String firstName = ind.getFirstName() != null ? ind.getFirstName() : "";
      String lastName = ind.getLastName() != null ? " " + ind.getLastName() : "";
      return (firstName + lastName).trim();
    }
    if (client instanceof Organization org) {
      return org.getLegalName();
    }
    return "";
  }

  List<PurchaseOrderResponseDTO> toResponseDTOList(List<PurchaseOrder> orders);
}
