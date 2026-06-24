package com.dispocol.dispofast.modules.iam.infra.persistence;

import com.dispocol.dispofast.modules.iam.domain.GoalType;
import com.dispocol.dispofast.modules.iam.domain.UserGoal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {

  List<UserGoal> findByUserIdAndTypeOrderByYearDescMonthDesc(UUID userId, GoalType type);

  @Query(
      "SELECT g FROM UserGoal g JOIN FETCH g.user "
          + "WHERE g.type = :type "
          + "AND (g.year > :year OR (g.year = :year AND g.month >= :month)) "
          + "ORDER BY g.year, g.month")
  List<UserGoal> findAllSalesQuotaFrom(
      @Param("year") int year, @Param("month") int month, @Param("type") GoalType type);
}
