package dev.xichen.backend.repository;

import dev.xichen.backend.entity.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {
    Optional<FinanceTransaction> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}
