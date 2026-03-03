package com.dispocol.dispofast.modules.customers.api.mappers;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.iam.api.mappers.UserMapper;
import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
import com.dispocol.dispofast.shared.location.api.mappers.LocationMapper;
import com.dispocol.dispofast.shared.location.domain.City;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, LocationMapper.class})
public interface ClientMapper {

  @Mapping(target = "name", ignore = true)
  @Mapping(target = "location", source = "city", qualifiedByName = "clientCityToLocation")
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

  @Named("clientCityToLocation")
  default LocationDTO clientCityToLocation(City city) {
    if (city == null) return null;
    LocationDTO dto = new LocationDTO();
    dto.setCityCode(city.getCode());
    dto.setCityName(city.getName());
    if (city.getDepartment() != null) {
      dto.setDepartmentCode(city.getDepartment().getCode());
      dto.setDepartmentName(city.getDepartment().getName());
    }
    return dto;
  }
}
