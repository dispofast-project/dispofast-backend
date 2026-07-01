package com.dispocol.dispofast.modules.shipping.infra.persistence;

import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrierRepository extends JpaRepository<Carrier, UUID> {}
