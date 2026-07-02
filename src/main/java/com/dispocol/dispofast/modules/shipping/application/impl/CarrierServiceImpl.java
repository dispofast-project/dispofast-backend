package com.dispocol.dispofast.modules.shipping.application.impl;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.CreateCarrierDTO;
import com.dispocol.dispofast.modules.shipping.api.mappers.CarrierMapper;
import com.dispocol.dispofast.modules.shipping.application.interfaces.CarrierService;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.CarrierNotFoundException;
import com.dispocol.dispofast.modules.shipping.infra.persistence.CarrierRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

  private final CarrierRepository carrierRepository;
  private final CarrierMapper carrierMapper;

  @Override
  @Transactional(readOnly = true)
  public CarrierResponseDTO getById(UUID id) {
    return carrierMapper.toResponseDTO(findEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CarrierResponseDTO> getAll(Pageable pageable) {
    return carrierRepository.findAll(pageable).map(carrierMapper::toResponseDTO);
  }

  @Override
  @Transactional
  public CarrierResponseDTO create(CreateCarrierDTO dto) {
    Carrier carrier = new Carrier();
    carrier.setName(dto.getName());
    carrier.setWebsite(dto.getWebsite());
    return carrierMapper.toResponseDTO(carrierRepository.save(carrier));
  }

  @Override
  @Transactional
  public CarrierResponseDTO update(UUID id, CreateCarrierDTO dto) {
    Carrier carrier = findEntityById(id);
    carrier.setName(dto.getName());
    carrier.setWebsite(dto.getWebsite());
    return carrierMapper.toResponseDTO(carrierRepository.save(carrier));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    findEntityById(id);
    carrierRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Carrier findEntityById(UUID id) {
    return carrierRepository
        .findById(id)
        .orElseThrow(
            () -> new CarrierNotFoundException("Transportista no encontrado con id: " + id));
  }
}
