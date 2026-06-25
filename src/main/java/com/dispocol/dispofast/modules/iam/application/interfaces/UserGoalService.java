package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserGoalDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserGoalDTO;
import com.dispocol.dispofast.modules.iam.domain.GoalType;
import java.util.List;
import java.util.UUID;

public interface UserGoalService {

  List<UserGoalDTO> getGoalsByType(UUID userId, GoalType type);

  UserGoalDTO createGoal(UUID userId, CreateUserGoalDTO dto);

  void deleteGoal(UUID userId, UUID goalId);
}
