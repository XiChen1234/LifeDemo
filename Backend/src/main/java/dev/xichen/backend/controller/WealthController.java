package dev.xichen.backend.controller;

import dev.xichen.backend.dto.WealthDtos;
import dev.xichen.backend.service.WealthService;
import dev.xichen.backend.view.PageViews;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wealth")
public class WealthController {
    private final WealthService wealthService;

    public WealthController(WealthService wealthService) {
        this.wealthService = wealthService;
    }

    @GetMapping("/overview")
    public PageViews.Wealth overview(@RequestParam(required = false) String month) {
        return wealthService.overview(month == null ? null : YearMonth.parse(month));
    }

    @GetMapping("/accounts")
    public WealthDtos.AccountsView accounts() {
        return wealthService.accounts();
    }

    @GetMapping("/categories")
    public List<WealthDtos.CategoryDto> categories() {
        return wealthService.categories();
    }

    @GetMapping("/transactions")
    public WealthDtos.TransactionPage transactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return wealthService.transactions(from, to, page, size);
    }

    @PostMapping("/transactions")
    public WealthDtos.TransactionDto create(@Valid @RequestBody WealthDtos.TransactionRequest request) {
        return wealthService.createTransaction(request);
    }

    @PatchMapping("/transactions/{id}")
    public WealthDtos.TransactionDto update(@PathVariable("id") Long id,
                                            @Valid @RequestBody WealthDtos.TransactionPatch patch) {
        return wealthService.updateTransaction(id, patch);
    }

    @DeleteMapping("/transactions/{id}")
    public void delete(@PathVariable("id") Long id) {
        wealthService.deleteTransaction(id);
    }

    @GetMapping("/budgets")
    public List<PageViews.Wealth.BudgetRow> budgets(@RequestParam(required = false) String month) {
        return wealthService.budgetsView(month == null ? null : YearMonth.parse(month));
    }

    @PutMapping("/budgets/{categoryId}")
    public PageViews.Wealth.BudgetRow updateBudget(@PathVariable("categoryId") Long categoryId,
                                                   @RequestParam(required = false) String month,
                                                   @Valid @RequestBody WealthDtos.BudgetLimitRequest request) {
        return wealthService.updateBudget(month == null ? null : YearMonth.parse(month), categoryId, request);
    }

    @GetMapping("/savings-plan")
    public WealthDtos.SavingsPlanView savingsPlan(@RequestParam(required = false) Integer year) {
        return wealthService.savingsPlan(year);
    }
}
