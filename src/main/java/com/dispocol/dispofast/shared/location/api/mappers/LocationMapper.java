package com.dispocol.dispofast.shared.location.api.mappers;

import com.dispocol.dispofast.shared.location.api.dto.LocationDTO;
import com.dispocol.dispofast.shared.location.domain.City;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

  @Mapping(target = "cityCode", source = "code")
  @Mapping(target = "cityName", source = "name")
  @Mapping(target = "departmentCode", source = "department.code")
  @Mapping(target = "departmentName", source = "department.name")
  LocationDTO toLocationDTO(City city);

  List<LocationDTO> toLocationDTOList(List<City> locations);
}
