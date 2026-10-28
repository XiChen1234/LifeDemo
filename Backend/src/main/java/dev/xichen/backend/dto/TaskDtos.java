package dev.xichen.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class TaskDtos {
    private TaskDtos() {
    }

    public record TaskDto(
            String id,
            String text,
            String title,
            String area,
            String priority,
            boolean done,
            String scheduledDate,
            String dueAt,
            String completedAt,
            int sortOrder,
            String createdAt,
            String updatedAt) {
    }

    public record TaskPage(List<TaskDto> items, int total, int page, int size, Stats stats) {
    }

    public record Stats(int activeCount, int dueSoonCount, int highPriorityOpen, int doneThisWeek) {
    }

    public record CreateRequest(
            @NotBlank @Size(max = 255) String title,
            @Size(max = 1000) String note,
            String area,
            String priority,
            LocalDate scheduledDate,
            LocalDateTime dueAt) {
    }

    public record PatchRequest(
            @Size(max = 255) String title,
            @Size(max = 1000) String note,
            String area,
            String priority,
            Boolean done,
            LocalDate scheduledDate,
            LocalDateTime dueAt) {
    }
}
