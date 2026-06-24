package com.dispocol.dispofast.modules.iam.application.impl;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserCommissionRateDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserCommissionRateDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserCommissionRateService;
import com.dispocol.dispofast.modules.iam.domain.UserCommissionRate;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserCommissionRateRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.inventory.infra.persistence.CategoryRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommissionRateServiceImpl implements UserCommissionRateService {

  private final UserCommissionRateRepository commissionRateRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public List<UserCommissionRateDTO> getCommissionRates(UUID userId) {
    return commissionRateRepository.findByUserId(userId).stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public UserCommissionRateDTO createCommissionRate(UUID userId, CreateUserCommissionRateDTO dto) {
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario solicitado."));

    var category =
        categoryRepository
            .findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("La categoría seleccionada no existe."));

    UserCommissionRate rate = new UserCommissionRate();
    rate.setUser(user);
    rate.setCategory(category);
    rate.setRate(dto.getRate());

    return toDTO(commissionRateRepository.save(rate));
  }

  @Override
  public void deleteCommissionRate(UUID userId, UUID rateId) {
    UserCommissionRate rate =
        commissionRateRepository
            .findById(rateId)
            .filter(r -> r.getUser().getId().equals(userId))
            .orElseThrow(() -> new UserNotFoundException("La comisión no fue encontrada."));
    commissionRateRepository.delete(rate);
  }

  private UserCommissionRateDTO toDTO(UserCommissionRate rate) {
    return new UserCommissionRateDTO(
        rate.getId(), rate.getCategory().getId(), rate.getCategory().getName(), rate.getRate());
  }
}
