package com.dispocol.dispofast.shared.location.api.mappers;

import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import com.dispocol.dispofast.shared.location.api.dto.DepartmentDTO;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.Department;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

  CityDTO toCityDTO(City city);

  DepartmentDTO toDepartmentDTO(Department department);

  List<CityDTO> toCityDTOList(List<City> locations);
}
