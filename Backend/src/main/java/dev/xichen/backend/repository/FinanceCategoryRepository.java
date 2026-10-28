package dev.xichen.backend.repository;

import dev.xichen.backend.entity.FinanceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinanceCategoryRepository extends JpaRepository<FinanceCategory, Long> {
    List<FinanceCategory> findByUserIdOrderBySortOrderAsc(Long userId);

    Optional<FinanceCategory> findByIdAndUserId(Long id, Long userId);
}
