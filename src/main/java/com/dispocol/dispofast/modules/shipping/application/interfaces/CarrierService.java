package com.dispocol.dispofast.modules.shipping.application.interfaces;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.CreateCarrierDTO;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarrierService {

  CarrierResponseDTO getById(UUID id);

  Page<CarrierResponseDTO> getAll(Pageable pageable);

  CarrierResponseDTO create(CreateCarrierDTO dto);

  CarrierResponseDTO update(UUID id, CreateCarrierDTO dto);

  void delete(UUID id);

  Carrier findEntityById(UUID id);
}
