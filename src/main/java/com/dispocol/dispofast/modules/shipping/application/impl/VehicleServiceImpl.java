package com.dispocol.dispofast.modules.shipping.application.impl;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateVehicleDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.VehicleResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.mappers.VehicleMapper;
import com.dispocol.dispofast.modules.shipping.application.interfaces.VehicleService;
import com.dispocol.dispofast.modules.shipping.domain.Vehicle;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.VehicleNotFoundException;
import com.dispocol.dispofast.modules.shipping.infra.persistence.VehicleRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;
  private final VehicleMapper vehicleMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<VehicleResponseDTO> getAll(Pageable pageable) {
    return vehicleRepository.findAll(pageable).map(vehicleMapper::toResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public VehicleResponseDTO getById(UUID id) {
    return vehicleMapper.toResponseDTO(findEntityById(id));
  }

  @Override
  @Transactional
  public VehicleResponseDTO create(CreateVehicleDTO dto) {
    if (vehicleRepository.findByPlate(dto.getPlate()).isPresent()) {
      throw new IllegalArgumentException("Ya existe un vehículo con la placa: " + dto.getPlate());
    }
    Vehicle vehicle = new Vehicle();
    vehicle.setPlate(dto.getPlate());
    vehicle.setState(dto.getState());
    vehicle.setType(dto.getType());
    return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
  }

  @Override
  @Transactional
  public VehicleResponseDTO update(UUID id, CreateVehicleDTO dto) {
    Vehicle vehicle = findEntityById(id);
    if (!vehicle.getPlate().equals(dto.getPlate())
        && vehicleRepository.findByPlate(dto.getPlate()).isPresent()) {
      throw new IllegalArgumentException("Ya existe un vehículo con la placa: " + dto.getPlate());
    }
    vehicle.setPlate(dto.getPlate());
    vehicle.setState(dto.getState());
    vehicle.setType(dto.getType());
    return vehicleMapper.toResponseDTO(vehicleRepository.save(vehicle));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    findEntityById(id);
    vehicleRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Vehicle findEntityById(UUID id) {
    return vehicleRepository
        .findById(id)
        .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado con id: " + id));
  }
}
