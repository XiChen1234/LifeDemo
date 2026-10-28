package dev.xichen.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class HealthDtos {
    private HealthDtos() {
    }

    public record SleepRecordDto(
            String id,
            LocalDate sleepDate,
            String sleepAt,
            String wakeAt,
            int durationMinutes,
            int deepMinutes,
            int lightMinutes,
            int remMinutes,
            int awakeMinutes,
            Integer qualityScore,
            String note) {
    }

    public record SleepRecordRequest(
            @NotNull LocalDateTime sleepAt,
            @NotNull LocalDateTime wakeAt,
            @Min(0) Integer deepMinutes,
            @Min(0) Integer lightMinutes,
            @Min(0) Integer remMinutes,
            @Min(0) Integer awakeMinutes,
            @Min(0) @Max(100) Integer qualityScore,
            String note) {
    }

    public record DailyActivityDto(
            LocalDate activityDate,
            int steps,
            int stepGoal,
            BigDecimal distanceKm,
            Integer activeCaloriesKcal) {
    }

    public record DailyActivityRequest(
            @Min(0) int steps,
            @Min(1) Integer stepGoal,
            @DecimalMin("0.0") BigDecimal distanceKm,
            @Min(0) Integer activeCaloriesKcal) {
    }

    public record WorkoutDto(
            String id,
            String workoutType,
            String startedAt,
            int durationMinutes,
            BigDecimal distanceKm,
            Integer caloriesKcal,
            String note) {
    }

    public record WorkoutRequest(
            @NotNull String workoutType,
            @NotNull LocalDateTime startedAt,
            @Min(1) int durationMinutes,
            @DecimalMin("0.0") BigDecimal distanceKm,
            @Min(0) Integer caloriesKcal,
            String note) {
    }

    public record BodyMetricDto(
            String id,
            LocalDate measuredOn,
            BigDecimal weightKg,
            BigDecimal bodyFatPercent,
            BigDecimal muscleKg) {
    }

    public record BodyMetricRequest(
            @NotNull LocalDate measuredOn,
            @NotNull @DecimalMin("20.0") BigDecimal weightKg,
            @DecimalMin("0.0") BigDecimal bodyFatPercent,
            @DecimalMin("0.0") BigDecimal muscleKg) {
    }

    public record HabitCheckInRequest(Boolean done, @DecimalMin("0.0") BigDecimal value) {
    }

    public record HabitCheckInResult(
            String habitId,
            LocalDate date,
            boolean done,
            String value,
            String targetValue,
            String unit,
            int percent) {
    }

    public record HabitList(List<HabitCheckInResult> items) {
    }
}
