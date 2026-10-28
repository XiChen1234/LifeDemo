package dev.xichen.backend.repository;

import dev.xichen.backend.entity.SavingsPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsPlanRepository extends JpaRepository<SavingsPlan, Long> {
    Optional<SavingsPlan> findByUserIdAndYear(Long userId, Integer year);
}
