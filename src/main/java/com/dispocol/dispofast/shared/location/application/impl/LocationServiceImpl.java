package com.dispocol.dispofast.shared.location.application.impl;

import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import com.dispocol.dispofast.shared.location.api.dto.DepartmentDTO;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.Department;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import com.dispocol.dispofast.shared.location.infra.persistence.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  private final CityRepository cityRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional(readOnly = true)
  public City findEntityById(String id) {
    return cityRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CityDTO> getAllCities(Pageable pageable, String departmentCode, String search) {
    String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
    String deptParam =
        (departmentCode != null && !departmentCode.isBlank()) ? departmentCode.trim() : null;
    return cityRepository.searchCities(deptParam, searchParam, pageable).map(this::toCityDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public CityDTO getCityByCode(String code) {
    City city =
        cityRepository
            .findById(code)
            .orElseThrow(() -> new ResourceNotFoundException("City not found with code: " + code));
    return toCityDTO(city);
  }

  @Override
  @Transactional(readOnly = true)
  public DepartmentDTO getDepartmentByCode(String code) {
    Department department =
        departmentRepository
            .findById(code)
            .orElseThrow(
                () -> new ResourceNotFoundException("Department not found with code: " + code));
    return toDepartmentDTO(department);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DepartmentDTO> getAllDepartments(Pageable pageable, String search) {
    String searchParam = (search != null && !search.isBlank()) ? search.trim() : null;
    return departmentRepository.searchDepartments(searchParam, pageable).map(this::toDepartmentDTO);
  }

  private CityDTO toCityDTO(City city) {
    return CityDTO.builder()
        .code(city.getCode())
        .name(city.getName())
        .department(city.getDepartment() != null ? toDepartmentDTO(city.getDepartment()) : null)
        .build();
  }

  private DepartmentDTO toDepartmentDTO(Department department) {
    return DepartmentDTO.builder().code(department.getCode()).name(department.getName()).build();
  }
}
