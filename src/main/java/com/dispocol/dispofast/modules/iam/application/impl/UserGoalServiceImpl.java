package com.dispocol.dispofast.modules.iam.application.impl;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserGoalDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserGoalDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserGoalService;
import com.dispocol.dispofast.modules.iam.domain.GoalType;
import com.dispocol.dispofast.modules.iam.domain.UserGoal;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserGoalRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGoalServiceImpl implements UserGoalService {

  private final UserGoalRepository userGoalRepository;
  private final UserRepository userRepository;

  @Override
  public List<UserGoalDTO> getGoalsByType(UUID userId, GoalType type) {
    return userGoalRepository.findByUserIdAndTypeOrderByYearDescMonthDesc(userId, type).stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public UserGoalDTO createGoal(UUID userId, CreateUserGoalDTO dto) {
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario solicitado."));

    UserGoal goal = new UserGoal();
    goal.setUser(user);
    goal.setType(dto.getType());
    goal.setMonth(dto.getMonth().shortValue());
    goal.setYear(dto.getYear().shortValue());
    goal.setValue(dto.getValue());

    return toDTO(userGoalRepository.save(goal));
  }

  @Override
  public void deleteGoal(UUID userId, UUID goalId) {
    UserGoal goal =
        userGoalRepository
            .findById(goalId)
            .filter(g -> g.getUser().getId().equals(userId))
            .orElseThrow(() -> new UserNotFoundException("La meta no fue encontrada."));
    userGoalRepository.delete(goal);
  }

  private UserGoalDTO toDTO(UserGoal goal) {
    return new UserGoalDTO(
        goal.getId(), goal.getType().name(), goal.getMonth(), goal.getYear(), goal.getValue());
  }
}
