package com.dispocol.dispofast.shared.location.application.interfaces;

import com.dispocol.dispofast.shared.location.domain.Location;

public interface LocationService {
  Location findEntityById(String id);
}
