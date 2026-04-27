package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateClientRequestDTO;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
  Page<ClientPreviewDTO> getAllClients(
      Pageable pageable, String text, String key, Boolean isActive, String city);

  ClientResponseDTO getClientById(UUID id);

  ClientResponseDTO createClient(CreateClientRequestDTO request, AppUser createdByUser);

  ClientResponseDTO updateClient(UUID id, CreateClientRequestDTO request);
}
