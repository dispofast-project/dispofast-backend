package com.dispocol.dispofast.modules.customers.api.mappers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.shared.location.api.mappers.CityMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, CityMapper.class})
public interface ClientMapper {

  @Mapping(target = "name", ignore = true)
  ClientPreviewDTO toPreviewDTO(Client client);

  @AfterMapping
  default void mapName(Client client, @MappingTarget ClientPreviewDTO dto) {
    if (client instanceof Individual individual) {
      String firstName = individual.getFirstName() != null ? individual.getFirstName() : "";
      String lastName = individual.getLastName() != null ? " " + individual.getLastName() : "";
      dto.setName((firstName + lastName).trim());
    } else if (client instanceof Organization organization) {
      dto.setName(organization.getLegalName());
    }
  }
}
