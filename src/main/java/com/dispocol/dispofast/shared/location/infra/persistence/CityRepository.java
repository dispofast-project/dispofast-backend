package com.dispocol.dispofast.shared.location.infra.persistence;

import com.dispocol.dispofast.shared.location.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, String> {}
