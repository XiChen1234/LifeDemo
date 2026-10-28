package dev.xichen.backend.repository;

import dev.xichen.backend.entity.SleepRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SleepRecordRepository extends JpaRepository<SleepRecord, Long> {
    List<SleepRecord> findByUserIdAndSleepDateBetweenOrderBySleepDateDesc(Long userId, LocalDate from, LocalDate to);

    Optional<SleepRecord> findTopByUserIdOrderBySleepDateDesc(Long userId);

    Optional<SleepRecord> findByIdAndUserId(Long id, Long userId);
}
