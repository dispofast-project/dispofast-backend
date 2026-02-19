package com.dispocol.dispofast.shared.location.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.Location;
import com.dispocol.dispofast.shared.location.infra.persistence.LocationRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional(readOnly = true)
    public Location findEntityById(String id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }
}
