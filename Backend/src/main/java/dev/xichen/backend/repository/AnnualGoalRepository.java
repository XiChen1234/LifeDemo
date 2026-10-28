package dev.xichen.backend.repository;

import dev.xichen.backend.entity.AnnualGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnualGoalRepository extends JpaRepository<AnnualGoal, Long> {
    List<AnnualGoal> findByUserIdAndYearOrderBySortOrderAsc(Long userId, Integer year);
}
