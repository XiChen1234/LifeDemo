package dev.xichen.backend.repository;

import dev.xichen.backend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndBudgetMonth(Long userId, LocalDate month);

    Optional<Budget> findByUserIdAndBudgetMonthAndCategoryId(Long userId, LocalDate month, Long categoryId);
}
