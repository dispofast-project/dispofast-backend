package com.dispocol.dispofast.shared.location.infra.seeder;

import com.dispocol.dispofast.shared.location.api.dto.CityResponse;
import com.dispocol.dispofast.shared.location.api.dto.DeptResponse;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.domain.Department;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import com.dispocol.dispofast.shared.location.infra.persistence.DepartmentRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class DataSeedInitializer implements CommandLineRunner {

  private final DepartmentRepository deptRepository;
  private final CityRepository cityRepository;
  private final RestTemplate restTemplate;

  private final String API_BASE_URL = "https://api-colombia.com/api/v1";

  public DataSeedInitializer(DepartmentRepository deptRepository, CityRepository cityRepository) {
    this.deptRepository = deptRepository;
    this.cityRepository = cityRepository;
    this.restTemplate = new RestTemplate();
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (deptRepository.count() > 4) {
      log.info("La base de datos ya contiene datos geográficos. Saltando Seed.");
      return;
    }

    log.info("Iniciando carga de Departamentos y Ciudades desde Colombia API...");

    try {
      // 1. Obtener Departamentos
      DeptResponse[] departments =
          restTemplate.getForObject(API_BASE_URL + "/Department", DeptResponse[].class);

      if (departments == null) return;

      for (DeptResponse deptDto : departments) {
        // Crear y guardar Departamento
        Department dept = new Department();
        dept.setCode(String.valueOf(deptDto.id()));
        dept.setName(deptDto.name());
        Department savedDept = deptRepository.save(dept);

        // 2. Obtener Ciudades para este Departamento
        CityResponse[] cities =
            restTemplate.getForObject(
                API_BASE_URL + "/Department/" + deptDto.id() + "/cities", CityResponse[].class);

        if (cities != null) {
          List<City> citiesToSave =
              Stream.of(cities)
                  .map(
                      cityDto -> {
                        City city = new City();
                        city.setCode(deptDto.id() + "-" + cityDto.id());
                        city.setName(cityDto.name());
                        city.setDepartment(savedDept);
                        return city;
                      })
                  .collect(Collectors.toList());

          cityRepository.saveAll(citiesToSave);
        }
        log.info(
            "Cargado: {} con {} ciudades.",
            savedDept.getName(),
            cities != null ? cities.length : 0);
      }
      log.info("Seed finalizado con éxito.");
    } catch (Exception e) {
      log.error("Error cargando datos de la API: {}", e.getMessage());
    }
  }
}
