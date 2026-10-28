package dev.xichen.backend.repository;

import dev.xichen.backend.entity.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {
    List<DailyActivity> findByUserIdAndActivityDateBetweenOrderByActivityDateDesc(Long userId, LocalDate from, LocalDate to);

    Optional<DailyActivity> findByUserIdAndActivityDate(Long userId, LocalDate date);
}
