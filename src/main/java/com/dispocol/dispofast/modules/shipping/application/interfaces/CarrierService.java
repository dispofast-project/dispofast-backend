package com.dispocol.dispofast.modules.shipping.application.interfaces;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.CreateCarrierDTO;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import java.util.List;
import java.util.UUID;

public interface CarrierService {

  CarrierResponseDTO getById(UUID id);

  List<CarrierResponseDTO> getAll();

  CarrierResponseDTO create(CreateCarrierDTO dto);

  CarrierResponseDTO update(UUID id, CreateCarrierDTO dto);

  void delete(UUID id);

  Carrier findEntityById(UUID id);

  Carrier findByPlate(String plate);
}
