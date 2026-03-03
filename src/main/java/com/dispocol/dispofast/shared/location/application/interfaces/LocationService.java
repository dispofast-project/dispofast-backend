package com.dispocol.dispofast.shared.location.application.interfaces;

import com.dispocol.dispofast.shared.location.domain.City;

public interface LocationService {
  City findEntityById(String id);
}
