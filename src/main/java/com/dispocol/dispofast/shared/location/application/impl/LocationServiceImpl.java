package com.dispocol.dispofast.shared.location.application.impl;

import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

  private final CityRepository locationRepository;

  @Override
  @Transactional(readOnly = true)
  public City findEntityById(String id) {
    return locationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
  }
}
