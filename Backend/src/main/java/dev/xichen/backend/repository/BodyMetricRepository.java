package dev.xichen.backend.repository;

import dev.xichen.backend.entity.BodyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BodyMetricRepository extends JpaRepository<BodyMetric, Long> {
    List<BodyMetric> findByUserIdAndMeasuredOnBetweenOrderByMeasuredOnDesc(Long userId, LocalDate from, LocalDate to);
}
