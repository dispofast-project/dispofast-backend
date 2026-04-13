package com.dispocol.dispofast.modules.customers.application.interfaces;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateClientRequestDTO;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ClientService {
  Page<ClientPreviewDTO> getAllClients(
      Pageable pageable, String text, String key, Boolean isActive, String city);

  ClientResponseDTO getClientById(UUID id);

  ClientResponseDTO createClient(CreateClientRequestDTO request, List<MultipartFile> documents);

  ClientResponseDTO updateClient(UUID id, CreateClientRequestDTO request, List<MultipartFile> documents);

  byte[] getLegalDocument(UUID clientId, UUID documentId);
}
