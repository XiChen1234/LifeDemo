package dev.xichen.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class WealthDtos {
    private WealthDtos() {
    }

    public record TransactionDto(
            String id,
            String type,
            String categoryId,
            String category,
            String title,
            String amount,
            String occurredAt,
            String note) {
    }

    public record TransactionRequest(
            @NotBlank String type,
            @NotNull Long categoryId,
            @NotBlank @Size(max = 128) String title,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDateTime occurredAt,
            @Size(max = 255) String note) {
    }

    public record TransactionPatch(
            String type,
            Long categoryId,
            @Size(max = 128) String title,
            @DecimalMin("0.01") BigDecimal amount,
            LocalDateTime occurredAt,
            @Size(max = 255) String note) {
    }

    public record TransactionPage(java.util.List<TransactionDto> items, int total, int page, int size) {
    }

    public record CategoryDto(String id, String name, String type, String color) {
    }

    public record AccountDto(String id, String name, String accountType, String balance, String currency) {
    }

    public record AccountsView(String totalAssets, java.util.List<AccountDto> accounts) {
    }

    public record BudgetDto(String categoryId, String name, long used, long total, int percent, String color) {
    }

    public record BudgetLimitRequest(@NotNull @DecimalMin("0.01") BigDecimal limitAmount) {
    }

    public record SavingsPlanView(
            int year,
            String annualTarget,
            String monthlyTarget,
            String annualSaved,
            double annualPercent,
            String monthlySaved,
            double monthlyPercent,
            int streakMonths) {
    }
}
