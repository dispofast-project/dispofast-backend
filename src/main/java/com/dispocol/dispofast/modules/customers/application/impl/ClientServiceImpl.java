package com.dispocol.dispofast.modules.customers.application.impl;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.mappers.ClientMapper;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<ClientPreviewDTO> getAllClients(Pageable pageable) {
    Page<Client> clientPage = clientRepository.findAll(pageable);
    return clientPage.map(clientMapper::toPreviewDTO);
  }
}
