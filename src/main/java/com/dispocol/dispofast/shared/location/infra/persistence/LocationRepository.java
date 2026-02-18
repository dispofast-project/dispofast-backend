package com.dispocol.dispofast.shared.location.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dispocol.dispofast.shared.location.domain.Location;

public interface LocationRepository extends JpaRepository<Location, UUID> {

}
