package com.dispocol.dispofast.modules.shipping.application.impl;

import com.dispocol.dispofast.modules.shipping.api.dtos.CarrierResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.CreateCarrierDTO;
import com.dispocol.dispofast.modules.shipping.api.mappers.CarrierMapper;
import com.dispocol.dispofast.modules.shipping.application.interfaces.CarrierService;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.CarrierNotFoundException;
import com.dispocol.dispofast.modules.shipping.infra.persistence.CarrierRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
  public List<CarrierResponseDTO> getAll() {
    return carrierRepository.findAll().stream().map(carrierMapper::toResponseDTO).toList();
  }

  @Override
  @Transactional
  public CarrierResponseDTO create(CreateCarrierDTO dto) {
    if (carrierRepository.findByPlate(dto.getPlate()).isPresent()) {
      throw new IllegalArgumentException(
          "Ya existe un transportista con la placa: " + dto.getPlate());
    }
    Carrier carrier = new Carrier();
    carrier.setName(dto.getName());
    carrier.setPlate(dto.getPlate());
    Carrier saved = carrierRepository.save(carrier);
    return carrierMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public CarrierResponseDTO update(UUID id, CreateCarrierDTO dto) {
    Carrier carrier = findEntityById(id);
    if (!carrier.getPlate().equals(dto.getPlate())
        && carrierRepository.findByPlate(dto.getPlate()).isPresent()) {
      throw new IllegalArgumentException(
          "Ya existe un transportista con la placa: " + dto.getPlate());
    }
    carrier.setName(dto.getName());
    carrier.setPlate(dto.getPlate());
    Carrier updated = carrierRepository.save(carrier);
    return carrierMapper.toResponseDTO(updated);
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

  @Override
  @Transactional(readOnly = true)
  public Carrier findByPlate(String plate) {
    return carrierRepository
        .findByPlate(plate)
        .orElseThrow(
            () -> new CarrierNotFoundException("Transportista no encontrado con placa: " + plate));
  }
}
