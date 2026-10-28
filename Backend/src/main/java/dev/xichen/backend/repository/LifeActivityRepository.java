package dev.xichen.backend.repository;

import dev.xichen.backend.entity.LifeActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LifeActivityRepository extends JpaRepository<LifeActivity, Long> {
    List<LifeActivity> findByUserIdAndOccurredAtBetweenOrderByOccurredAtDesc(Long userId, LocalDateTime from, LocalDateTime to);

    Optional<LifeActivity> findByIdAndUserId(Long id, Long userId);
}
