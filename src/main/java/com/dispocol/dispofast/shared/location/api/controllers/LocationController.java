package com.dispocol.dispofast.shared.location.api.controllers;

import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import com.dispocol.dispofast.shared.location.api.dto.DepartmentDTO;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  // ── Cities ──

  @GetMapping("/cities")
  public ResponseEntity<Page<CityDTO>> getAllCities(
      Pageable pageable,
      @RequestParam(required = false) String departmentCode,
      @RequestParam(required = false) String search) {
    return ResponseEntity.ok(locationService.getAllCities(pageable, departmentCode, search));
  }

  @GetMapping("/cities/{code}")
  public ResponseEntity<CityDTO> getCityByCode(@PathVariable String code) {
    return ResponseEntity.ok(locationService.getCityByCode(code));
  }

  // ── Departments ──

  @GetMapping("/departments")
  public ResponseEntity<Page<DepartmentDTO>> getAllDepartments(
      Pageable pageable, @RequestParam(required = false) String search) {
    return ResponseEntity.ok(locationService.getAllDepartments(pageable, search));
  }

  @GetMapping("/departments/{code}")
  public ResponseEntity<DepartmentDTO> getDepartmentByCode(@PathVariable String code) {
    return ResponseEntity.ok(locationService.getDepartmentByCode(code));
  }
}
