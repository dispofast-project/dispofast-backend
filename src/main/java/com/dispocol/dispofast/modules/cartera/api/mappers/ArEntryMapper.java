package com.dispocol.dispofast.modules.cartera.api.mappers;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArEntryMapper {

  @Mapping(
      target = "clientName",
      expression = "java(entry.getClient().getDisplayName())")
  @Mapping(target = "clientIdentification", source = "client.identificationNumber")
  @Mapping(
      target = "asesorName",
      expression =
          "java(entry.getAsesor() != null ? entry.getAsesor().getFullName() : null)")
  @Mapping(
      target = "orderNumber",
      expression =
          "java(entry.getOrder() != null ? entry.getOrder().getOrderNumber() : null)")
  @Mapping(
      target = "diasCartera",
      expression = "java(entry.getDiasCartera())")
  @Mapping(
      target = "diasVencimiento",
      expression = "java(entry.getDiasVencimiento())")
  @Mapping(
      target = "cityName",
      expression =
          "java(entry.getCity() != null ? entry.getCity().getName() : null)")
  ArEntryResponseDTO toResponseDTO(ArEntry entry);

  List<ArEntryResponseDTO> toResponseDTOList(List<ArEntry> entries);
}
