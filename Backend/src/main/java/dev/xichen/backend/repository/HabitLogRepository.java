package dev.xichen.backend.repository;

import dev.xichen.backend.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    Optional<HabitLog> findByHabitIdAndLogDate(Long habitId, LocalDate date);

    List<HabitLog> findByUserIdAndLogDate(Long userId, LocalDate date);

    List<HabitLog> findByUserIdAndLogDateBetween(Long userId, LocalDate from, LocalDate to);
}
