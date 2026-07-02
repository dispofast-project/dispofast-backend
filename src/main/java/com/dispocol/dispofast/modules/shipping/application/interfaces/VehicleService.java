package com.dispocol.dispofast.modules.shipping.application.interfaces;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateVehicleDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.VehicleResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Vehicle;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {

  Page<VehicleResponseDTO> getAll(Pageable pageable);

  VehicleResponseDTO getById(UUID id);

  VehicleResponseDTO create(CreateVehicleDTO dto);

  VehicleResponseDTO update(UUID id, CreateVehicleDTO dto);

  void delete(UUID id);

  Vehicle findEntityById(UUID id);
}
