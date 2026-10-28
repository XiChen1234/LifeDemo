package dev.xichen.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class LifeDtos {
    private LifeDtos() {
    }

    public record ActivityDto(
            String id,
            String activityType,
            String title,
            String occurredAt,
            Integer durationMinutes,
            String note) {
    }

    public record ActivityRequest(
            @NotBlank String activityType,
            @NotBlank @Size(max = 128) String title,
            @NotNull LocalDateTime occurredAt,
            @Min(0) Integer durationMinutes,
            @Size(max = 255) String note) {
    }

    public record MediaDto(
            String id,
            String mediaType,
            String title,
            String subtitle,
            String creator,
            String status,
            String progressCurrent,
            String progressTotal,
            String progressUnit,
            int percent,
            Integer rating,
            String startedOn,
            String finishedOn) {
    }

    public record MediaRequest(
            @NotBlank String mediaType,
            @NotBlank @Size(max = 255) String title,
            @Size(max = 255) String subtitle,
            @Size(max = 128) String creator,
            String status,
            @DecimalMin("0.0") BigDecimal progressCurrent,
            @DecimalMin("0.0") BigDecimal progressTotal,
            String progressUnit,
            @Min(0) @Max(5) Integer rating,
            LocalDate startedOn,
            LocalDate finishedOn) {
    }

    public record MediaPatch(
            @Size(max = 255) String title,
            @Size(max = 255) String subtitle,
            String status,
            @DecimalMin("0.0") BigDecimal progressCurrent,
            @DecimalMin("0.0") BigDecimal progressTotal,
            String progressUnit,
            @Min(0) @Max(5) Integer rating,
            LocalDate startedOn,
            LocalDate finishedOn) {
    }
}
