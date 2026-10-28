package dev.xichen.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class WorkDtos {
    private WorkDtos() {
    }

    public record ProjectDto(
            String id,
            String name,
            String stage,
            double progressPercent,
            String status,
            LocalDate startDate,
            LocalDate targetDate,
            String color) {
    }

    public record ProjectRequest(
            @NotBlank @Size(max = 128) String name,
            @Size(max = 64) String stage,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal progressPercent,
            String status,
            LocalDate startDate,
            LocalDate targetDate,
            String color) {
    }

    public record CalendarEventRow(int day, String text, String level, Boolean isDdl) {
    }
}
