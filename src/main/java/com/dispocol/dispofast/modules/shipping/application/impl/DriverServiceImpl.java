package com.dispocol.dispofast.modules.shipping.application.impl;

import com.dispocol.dispofast.modules.shipping.api.dtos.CreateDriverDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.DriverResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.mappers.DriverMapper;
import com.dispocol.dispofast.modules.shipping.application.interfaces.DriverService;
import com.dispocol.dispofast.modules.shipping.domain.Driver;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.DriverNotFoundException;
import com.dispocol.dispofast.modules.shipping.infra.persistence.DriverRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final DriverMapper driverMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<DriverResponseDTO> getAll(Pageable pageable) {
    return driverRepository.findAll(pageable).map(driverMapper::toResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public DriverResponseDTO getById(UUID id) {
    return driverMapper.toResponseDTO(findEntityById(id));
  }

  @Override
  @Transactional
  public DriverResponseDTO create(CreateDriverDTO dto) {
    Driver driver = new Driver();
    driver.setName(dto.getName());
    driver.setPhone(dto.getPhone());
    driver.setCedula(dto.getCedula());
    return driverMapper.toResponseDTO(driverRepository.save(driver));
  }

  @Override
  @Transactional
  public DriverResponseDTO update(UUID id, CreateDriverDTO dto) {
    Driver driver = findEntityById(id);
    driver.setName(dto.getName());
    driver.setPhone(dto.getPhone());
    driver.setCedula(dto.getCedula());
    return driverMapper.toResponseDTO(driverRepository.save(driver));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    findEntityById(id);
    driverRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Driver findEntityById(UUID id) {
    return driverRepository
        .findById(id)
        .orElseThrow(() -> new DriverNotFoundException("Conductor no encontrado con id: " + id));
  }
}
