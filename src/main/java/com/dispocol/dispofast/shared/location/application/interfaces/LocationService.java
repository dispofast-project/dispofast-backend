package com.dispocol.dispofast.shared.location.application.interfaces;

import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import com.dispocol.dispofast.shared.location.api.dto.DepartmentDTO;
import com.dispocol.dispofast.shared.location.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationService {
  City findEntityById(String id);

  Page<CityDTO> getAllCities(Pageable pageable, String departmentCode, String search);

  CityDTO getCityByCode(String code);

  DepartmentDTO getDepartmentByCode(String code);

  Page<DepartmentDTO> getAllDepartments(Pageable pageable, String search);
}
