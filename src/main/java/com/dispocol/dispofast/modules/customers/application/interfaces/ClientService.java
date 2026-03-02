package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
  Page<ClientPreviewDTO> getAllClients(Pageable pageable);
}
