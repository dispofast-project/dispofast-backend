package com.dispocol.dispofast.modules.shipping.infra.persistence;

import com.dispocol.dispofast.modules.shipping.domain.Vehicle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

  Optional<Vehicle> findByPlate(String plate);
}
