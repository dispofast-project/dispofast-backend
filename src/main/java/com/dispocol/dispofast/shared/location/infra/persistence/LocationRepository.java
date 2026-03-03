package com.dispocol.dispofast.shared.location.infra.persistence;

import com.dispocol.dispofast.shared.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {}
