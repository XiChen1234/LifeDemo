package dev.xichen.backend.repository;

import dev.xichen.backend.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndActiveTrueOrderBySortOrderAsc(Long userId);
}
