package com.dispocol.dispofast.modules.shipping.application.interfaces;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateDriverDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.DriverResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Driver;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DriverService {

  Page<DriverResponseDTO> getAll(Pageable pageable);

  DriverResponseDTO getById(UUID id);

  DriverResponseDTO create(CreateDriverDTO dto);

  DriverResponseDTO update(UUID id, CreateDriverDTO dto);

  void delete(UUID id);

  Driver findEntityById(UUID id);
}
