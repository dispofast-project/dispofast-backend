package com.dispocol.dispofast.shared.location.infra.persistence;

import com.dispocol.dispofast.shared.location.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CityRepository extends JpaRepository<City, String> {

  @Query(
      "SELECT c FROM City c WHERE "
          + "(:departmentCode IS NULL OR c.department.code = :departmentCode) AND "
          + "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<City> searchCities(
      @Param("departmentCode") String departmentCode,
      @Param("search") String search,
      Pageable pageable);
}
