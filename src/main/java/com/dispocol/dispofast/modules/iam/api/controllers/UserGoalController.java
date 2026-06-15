package com.dispocol.dispofast.modules.iam.api.controllers;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserGoalDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserGoalDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserGoalService;
import com.dispocol.dispofast.modules.iam.domain.GoalType;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/goals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserGoalController {

  private final UserGoalService userGoalService;

  @GetMapping
  public ResponseEntity<List<UserGoalDTO>> getGoals(
      @PathVariable UUID userId, @RequestParam GoalType type) {
    return ResponseEntity.ok(userGoalService.getGoalsByType(userId, type));
  }

  @PostMapping
  public ResponseEntity<UserGoalDTO> createGoal(
      @PathVariable UUID userId, @Valid @RequestBody CreateUserGoalDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userGoalService.createGoal(userId, dto));
  }

  @DeleteMapping("/{goalId}")
  public ResponseEntity<Void> deleteGoal(
      @PathVariable UUID userId, @PathVariable UUID goalId) {
    userGoalService.deleteGoal(userId, goalId);
    return ResponseEntity.noContent().build();
  }
}
