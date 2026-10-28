package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.common.Styles;
import dev.xichen.backend.common.Views;
import dev.xichen.backend.dto.WealthDtos;
import dev.xichen.backend.entity.Account;
import dev.xichen.backend.entity.Budget;
import dev.xichen.backend.entity.FinanceCategory;
import dev.xichen.backend.entity.FinanceTransaction;
import dev.xichen.backend.entity.SavingsPlan;
import dev.xichen.backend.repository.AccountRepository;
import dev.xichen.backend.repository.BudgetRepository;
import dev.xichen.backend.repository.FinanceCategoryRepository;
import dev.xichen.backend.repository.FinanceTransactionRepository;
import dev.xichen.backend.repository.SavingsPlanRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WealthService {
    private final JdbcTemplate jdbc;
    private final AccountRepository accountRepository;
    private final FinanceCategoryRepository categoryRepository;
    private final FinanceTransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsPlanRepository savingsPlanRepository;

    public WealthService(JdbcTemplate jdbc, AccountRepository accountRepository,
                         FinanceCategoryRepository categoryRepository,
                         FinanceTransactionRepository transactionRepository,
                         BudgetRepository budgetRepository, SavingsPlanRepository savingsPlanRepository) {
        this.jdbc = jdbc;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.savingsPlanRepository = savingsPlanRepository;
    }

    public PageViews.Wealth overview(YearMonth requested) {
        YearMonth month = requested == null ? YearMonth.now() : requested;
        long userId = CurrentUser.ID;
        LocalDate first = month.atDay(1);
        LocalDate end = first.plusMonths(1);
        LocalDateTime from = first.atStartOfDay();
        LocalDateTime to = end.atStartOfDay();

        BigDecimal totalAssets = sum("SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE user_id = ? AND is_active = 1", userId);
        BigDecimal income = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'income' AND occurred_at >= ? AND occurred_at < ?", userId, from, to);
        BigDecimal expense = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?", userId, from, to);
        BigDecimal balance = income.subtract(expense);
        double savingsRate = income.signum() == 0 ? 0 : balance.doubleValue() / income.doubleValue() * 100;

        BigDecimal prevExpense = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?", userId, from.minusMonths(1), from);
        double expenseChange = prevExpense.signum() == 0 ? 0
                : expense.subtract(prevExpense).doubleValue() / prevExpense.doubleValue() * 100;
        BigDecimal prevIncome = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'income' AND occurred_at >= ? AND occurred_at < ?", userId, from.minusMonths(1), from);
        double incomeChange = prevIncome.signum() == 0 ? 0
                : income.subtract(prevIncome).doubleValue() / prevIncome.doubleValue() * 100;

        BigDecimal budgetTotal = sum("SELECT COALESCE(SUM(limit_amount), 0) FROM budgets WHERE user_id = ? AND budget_month = ?", userId, first);
        int budgetUsage = Formats.percent(expense.doubleValue(), budgetTotal.doubleValue());

        String bannerTitle = balance.signum() >= 0 ? "本月收支健康" : "本月支出偏高";
        String bannerDesc = "本月结余 " + Formats.money(balance) + "，储蓄率 " + Formats.oneDecimal(savingsRate)
                + "%，预算已用 " + budgetUsage + "%。";

        PageViews.Wealth wealth = new PageViews.Wealth(
                new PageViews.Wealth.Banner(
                        bannerTitle,
                        bannerDesc,
                        List.of(month.getMonthValue() + " 月", "收入 " + Formats.money(income), "支出 " + Formats.money(expense)),
                        totalAssets.setScale(0, RoundingMode.HALF_UP).intValue(),
                        balance.setScale(0, RoundingMode.HALF_UP).intValue(),
                        round1(savingsRate)),
                List.of(
                        new Views.Metric("assets", "总资产", Formats.money(totalAssets), "up",
                                "+" + Formats.money(balance), "本月结余"),
                        new Views.Metric("income", "本月收入", Formats.money(income),
                                incomeChange >= 0 ? "up" : "down",
                                String.format("%+.1f%%", incomeChange), "较上月"),
                        new Views.Metric("expense", "本月支出", Formats.money(expense),
                                expenseChange <= 0 ? "down" : "up",
                                String.format("%+.1f%%", expenseChange), "较上月"),
                        new Views.Metric("balance", "本月结余", Formats.money(balance),
                                balance.signum() >= 0 ? "up" : "down",
                                Formats.oneDecimal(savingsRate) + "%", "储蓄率")),
                monthCash(userId, first, end),
                expense6m(userId, month),
                expenseCategories(userId, from, to),
                savingGoals(userId, month),
                transactions(userId, from, to),
                budgets(userId, first));

        return wealth;
    }

    private PageViews.Wealth.MonthCash monthCash(long userId, LocalDate first, LocalDate end) {
        List<String> labels = new ArrayList<>();
        List<Integer> income = new ArrayList<>();
        List<Integer> expense = new ArrayList<>();
        LocalDate weekStart = first.minusDays(first.getDayOfWeek().getValue() - 1L);
        int index = 1;
        while (weekStart.isBefore(end)) {
            LocalDate start = weekStart.isBefore(first) ? first : weekStart;
            LocalDate next = weekStart.plusDays(7);
            LocalDate stop = next.isAfter(end) ? end : next;
            labels.add("第 " + index + " 周");
            income.add(scaled(sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'income' AND occurred_at >= ? AND occurred_at < ?", userId, start.atStartOfDay(), stop.atStartOfDay())));
            expense.add(scaled(sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?", userId, start.atStartOfDay(), stop.atStartOfDay())));
            weekStart = next;
            index++;
        }
        return new PageViews.Wealth.MonthCash(labels, income, expense);
    }

    private PageViews.Wealth.Chart expense6m(long userId, YearMonth month) {
        YearMonth start = month.minusMonths(5);
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth current = start.plusMonths(i);
            labels.add(Formats.monthShort(current.getMonthValue()));
            data.add(scaled(sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?",
                    userId, current.atDay(1).atStartOfDay(), current.plusMonths(1).atDay(1).atStartOfDay())));
        }
        return new PageViews.Wealth.Chart(labels, data);
    }

    private List<PageViews.Wealth.CategorySlice> expenseCategories(long userId, LocalDateTime from, LocalDateTime to) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT c.name, SUM(t.amount) AS total
                        FROM transactions t
                        JOIN finance_categories c ON c.id = t.category_id
                        WHERE t.user_id = ? AND t.type = 'expense' AND t.deleted_at IS NULL
                          AND t.occurred_at >= ? AND t.occurred_at < ?
                        GROUP BY c.id, c.name
                        ORDER BY total DESC
                        LIMIT 6
                        """, userId, from, to);
        List<PageViews.Wealth.CategorySlice> slices = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            slices.add(new PageViews.Wealth.CategorySlice((String) row.get("name"),
                    ((BigDecimal) row.get("total")).setScale(0, RoundingMode.HALF_UP).intValue()));
        }
        return slices;
    }

    private List<Views.Goal> savingGoals(long userId, YearMonth month) {
        int year = month.getYear();
        BigDecimal annualTarget = savingsPlanRepository.findByUserIdAndYear(userId, year)
                .map(SavingsPlan::getAnnualTarget).orElse(BigDecimal.ZERO);
        BigDecimal monthlyTarget = savingsPlanRepository.findByUserIdAndYear(userId, year)
                .map(SavingsPlan::getMonthlyTarget).orElse(BigDecimal.ZERO);

        BigDecimal annualSaved = savings(userId, LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1));
        BigDecimal monthlySaved = savings(userId, month.atDay(1), month.plusMonths(1).atDay(1));
        int streak = streakMonths(userId, month, monthlyTarget);

        return List.of(
                new Views.Goal("年度储蓄", Formats.percentRaw(annualSaved.doubleValue(), annualTarget.doubleValue()),
                        Formats.oneDecimal(annualSaved.doubleValue() / Math.max(annualTarget.doubleValue(), 1) * 100) + "%",
                        Formats.money(annualSaved) + " / " + Formats.money(annualTarget), "green"),
                new Views.Goal("月度储蓄", Formats.percentRaw(monthlySaved.doubleValue(), monthlyTarget.doubleValue()),
                        Formats.oneDecimal(monthlySaved.doubleValue() / Math.max(monthlyTarget.doubleValue(), 1) * 100) + "%",
                        Formats.money(monthlySaved) + " / " + Formats.money(monthlyTarget), "yellow"),
                new Views.Goal("连续达标", Math.min(100, streak * 20),
                        streak + " 个月", "目标每月 " + Formats.money(monthlyTarget), "blue"));
    }

    private int streakMonths(long userId, YearMonth month, BigDecimal monthlyTarget) {
        if (monthlyTarget.signum() <= 0) {
            return 0;
        }
        int streak = 0;
        YearMonth cursor = month;
        for (int i = 0; i < 24; i++) {
            BigDecimal saved = savings(userId, cursor.atDay(1), cursor.plusMonths(1).atDay(1));
            if (saved.compareTo(monthlyTarget) >= 0) {
                streak++;
                cursor = cursor.minusMonths(1);
            } else {
                break;
            }
        }
        return streak;
    }

    private List<PageViews.Wealth.TxRow> transactions(long userId, LocalDateTime from, LocalDateTime to) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT t.id, t.type, t.title, t.amount, t.occurred_at, c.name AS category
                        FROM transactions t
                        JOIN finance_categories c ON c.id = t.category_id
                        WHERE t.user_id = ? AND t.deleted_at IS NULL AND t.occurred_at >= ? AND t.occurred_at < ?
                        ORDER BY t.occurred_at DESC
                        LIMIT 6
                        """, userId, from, to);
        List<PageViews.Wealth.TxRow> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String category = (String) row.get("category");
            boolean isIncome = "income".equals(row.get("type"));
            items.add(new PageViews.Wealth.TxRow(
                    String.valueOf(row.get("id")),
                    category,
                    Styles.categoryPill(category),
                    (String) row.get("title"),
                    Formats.transactionTime((LocalDateTime) row.get("occurred_at")),
                    Formats.signedMoney((BigDecimal) row.get("amount"), isIncome),
                    isIncome));
        }
        return items;
    }

    private List<PageViews.Wealth.BudgetRow> budgets(long userId, LocalDate monthStart) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT b.category_id, c.name, c.color, b.limit_amount,
                               COALESCE((SELECT SUM(t.amount) FROM transactions t
                                         WHERE t.user_id = b.user_id AND t.category_id = b.category_id
                                           AND t.type = 'expense' AND t.deleted_at IS NULL
                                           AND t.occurred_at >= ? AND t.occurred_at < ?), 0) AS used
                        FROM budgets b
                        JOIN finance_categories c ON c.id = b.category_id
                        WHERE b.user_id = ? AND b.budget_month = ?
                        ORDER BY c.sort_order
                        """, monthStart.atStartOfDay(), monthStart.plusMonths(1).atStartOfDay(), userId, monthStart);
        List<PageViews.Wealth.BudgetRow> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            long used = ((BigDecimal) row.get("used")).setScale(0, RoundingMode.HALF_UP).longValue();
            long total = ((BigDecimal) row.get("limit_amount")).setScale(0, RoundingMode.HALF_UP).longValue();
            int percent = Formats.percent(used, total);
            String color = percent >= 90 ? "danger" : percent >= 70 ? "yellow" : Styles.barColor((String) row.get("color"));
            items.add(new PageViews.Wealth.BudgetRow((String) row.get("name"), used, total, percent, color));
        }
        return items;
    }

    // ---------- 账户与分类 ----------

    public List<WealthDtos.CategoryDto> categories() {
        return categoryRepository.findByUserIdOrderBySortOrderAsc(CurrentUser.ID).stream()
                .map(category -> new WealthDtos.CategoryDto(String.valueOf(category.getId()), category.getName(),
                        category.getType(), category.getColor()))
                .toList();
    }

    public WealthDtos.AccountsView accounts() {
        List<Account> accounts = accountRepository.findByUserIdAndActiveTrueOrderBySortOrderAsc(CurrentUser.ID);
        BigDecimal total = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new WealthDtos.AccountsView(Formats.money(total), accounts.stream()
                .map(account -> new WealthDtos.AccountDto(String.valueOf(account.getId()), account.getName(),
                        account.getAccountType(), Formats.money(account.getBalance()), account.getCurrency()))
                .toList());
    }

    // ---------- 交易 ----------

    public WealthDtos.TransactionPage transactions(LocalDate from, LocalDate to, int page, int size) {
        long userId = CurrentUser.ID;
        LocalDateTime start = (from == null ? LocalDate.now().withDayOfMonth(1) : from).atStartOfDay();
        LocalDateTime end = (to == null ? LocalDate.now().plusDays(1) : to.plusDays(1)).atStartOfDay();
        int offset = Math.max(0, (page - 1) * size);
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT t.id, t.type, t.category_id, t.title, t.amount, t.occurred_at, t.note, c.name AS category
                        FROM transactions t
                        JOIN finance_categories c ON c.id = t.category_id
                        WHERE t.user_id = ? AND t.deleted_at IS NULL AND t.occurred_at >= ? AND t.occurred_at < ?
                        ORDER BY t.occurred_at DESC
                        LIMIT ? OFFSET ?
                        """, userId, start, end, size, offset);
        Integer total = jdbc.queryForObject("""
                SELECT COUNT(*) FROM transactions
                WHERE user_id = ? AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?
                """, Integer.class, userId, start, end);
        List<WealthDtos.TransactionDto> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            items.add(new WealthDtos.TransactionDto(
                    String.valueOf(row.get("id")),
                    (String) row.get("type"),
                    String.valueOf(row.get("category_id")),
                    (String) row.get("category"),
                    (String) row.get("title"),
                    Formats.signedMoney((BigDecimal) row.get("amount"), "income".equals(row.get("type"))),
                    Formats.offset((LocalDateTime) row.get("occurred_at")).toString(),
                    (String) row.get("note")));
        }
        return new WealthDtos.TransactionPage(items, total == null ? 0 : total, page, size);
    }

    @Transactional
    public WealthDtos.TransactionDto createTransaction(WealthDtos.TransactionRequest request) {
        long userId = CurrentUser.ID;
        FinanceCategory category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                .orElseThrow(() -> ApiException.notFound("分类不存在"));
        if (!category.getType().equals(request.type())) {
            throw ApiException.badRequest("分类类型与交易方向不匹配");
        }
        FinanceTransaction transaction = new FinanceTransaction();
        transaction.setUserId(userId);
        transaction.setType(request.type());
        transaction.setCategoryId(category.getId());
        transaction.setTitle(request.title());
        transaction.setAmount(request.amount());
        transaction.setOccurredAt(request.occurredAt());
        transaction.setNote(request.note());
        transaction.setSource("manual");
        FinanceTransaction saved = transactionRepository.save(transaction);
        adjustAccount(userId, request.type(), request.amount(), false);
        return toDto(saved, category.getName());
    }

    @Transactional
    public WealthDtos.TransactionDto updateTransaction(Long id, WealthDtos.TransactionPatch patch) {
        long userId = CurrentUser.ID;
        FinanceTransaction transaction = transactionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> ApiException.notFound("交易不存在"));
        adjustAccount(userId, transaction.getType(), transaction.getAmount(), true);

        String type = patch.type() == null ? transaction.getType() : patch.type();
        Long categoryId = patch.categoryId() == null ? transaction.getCategoryId() : patch.categoryId();
        FinanceCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> ApiException.notFound("分类不存在"));
        if (!category.getType().equals(type)) {
            throw ApiException.badRequest("分类类型与交易方向不匹配");
        }
        transaction.setType(type);
        transaction.setCategoryId(category.getId());
        if (patch.title() != null) {
            transaction.setTitle(patch.title());
        }
        if (patch.amount() != null) {
            transaction.setAmount(patch.amount());
        }
        if (patch.occurredAt() != null) {
            transaction.setOccurredAt(patch.occurredAt());
        }
        if (patch.note() != null) {
            transaction.setNote(patch.note());
        }
        FinanceTransaction saved = transactionRepository.save(transaction);
        adjustAccount(userId, saved.getType(), saved.getAmount(), false);
        return toDto(saved, category.getName());
    }

    @Transactional
    public void deleteTransaction(Long id) {
        long userId = CurrentUser.ID;
        FinanceTransaction transaction = transactionRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> ApiException.notFound("交易不存在"));
        transaction.setDeletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        adjustAccount(userId, transaction.getType(), transaction.getAmount(), true);
    }

    private void adjustAccount(long userId, String type, BigDecimal amount, boolean reverse) {
        List<Account> accounts = accountRepository.findByUserIdAndActiveTrueOrderBySortOrderAsc(userId);
        if (accounts.isEmpty()) {
            return;
        }
        Account account = accounts.get(0);
        BigDecimal delta = "income".equals(type) ? amount : amount.negate();
        account.setBalance(account.getBalance().add(reverse ? delta.negate() : delta));
        accountRepository.save(account);
    }

    private WealthDtos.TransactionDto toDto(FinanceTransaction transaction, String categoryName) {
        return new WealthDtos.TransactionDto(String.valueOf(transaction.getId()), transaction.getType(),
                String.valueOf(transaction.getCategoryId()), categoryName, transaction.getTitle(),
                Formats.signedMoney(transaction.getAmount(), "income".equals(transaction.getType())),
                Formats.offset(transaction.getOccurredAt()).toString(), transaction.getNote());
    }

    // ---------- 预算与储蓄 ----------

    public List<PageViews.Wealth.BudgetRow> budgetsView(YearMonth month) {
        return budgets(CurrentUser.ID, (month == null ? YearMonth.now() : month).atDay(1));
    }

    @Transactional
    public PageViews.Wealth.BudgetRow updateBudget(YearMonth month, Long categoryId, WealthDtos.BudgetLimitRequest request) {
        long userId = CurrentUser.ID;
        LocalDate monthStart = (month == null ? YearMonth.now() : month).atDay(1);
        FinanceCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> ApiException.notFound("分类不存在"));
        Budget budget = budgetRepository.findByUserIdAndBudgetMonthAndCategoryId(userId, monthStart, categoryId)
                .orElseGet(Budget::new);
        budget.setUserId(userId);
        budget.setBudgetMonth(monthStart);
        budget.setCategoryId(categoryId);
        budget.setLimitAmount(request.limitAmount());
        budgetRepository.save(budget);
        return budgets(userId, monthStart).stream()
                .filter(row -> row.name().equals(category.getName()))
                .findFirst()
                .orElseGet(() -> new PageViews.Wealth.BudgetRow(category.getName(), 0,
                        request.limitAmount().intValue(), 0, category.getColor()));
    }

    public WealthDtos.SavingsPlanView savingsPlan(Integer year) {
        int targetYear = year == null ? YearMonth.now().getYear() : year;
        long userId = CurrentUser.ID;
        SavingsPlan plan = savingsPlanRepository.findByUserIdAndYear(userId, targetYear)
                .orElseGet(() -> {
                    SavingsPlan fresh = new SavingsPlan();
                    fresh.setUserId(userId);
                    fresh.setYear(targetYear);
                    fresh.setAnnualTarget(BigDecimal.ZERO);
                    fresh.setMonthlyTarget(BigDecimal.ZERO);
                    return fresh;
                });
        BigDecimal annualSaved = savings(userId, LocalDate.of(targetYear, 1, 1), LocalDate.of(targetYear + 1, 1, 1));
        BigDecimal monthlySaved = savings(userId, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).plusMonths(1));
        return new WealthDtos.SavingsPlanView(targetYear,
                Formats.money(plan.getAnnualTarget()), Formats.money(plan.getMonthlyTarget()),
                Formats.money(annualSaved), Formats.percentRaw(annualSaved.doubleValue(), plan.getAnnualTarget().doubleValue()),
                Formats.money(monthlySaved), Formats.percentRaw(monthlySaved.doubleValue(), plan.getMonthlyTarget().doubleValue()),
                streakMonths(userId, YearMonth.now(), plan.getMonthlyTarget()));
    }

    // ---------- 工具 ----------

    private BigDecimal savings(long userId, LocalDate from, LocalDate to) {
        BigDecimal income = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'income' AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?",
                userId, from.atStartOfDay(), to.atStartOfDay());
        BigDecimal expense = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'expense' AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?",
                userId, from.atStartOfDay(), to.atStartOfDay());
        return income.subtract(expense);
    }

    private BigDecimal sum(String sql, Object... args) {
        BigDecimal value = jdbc.queryForObject(sql, BigDecimal.class, args);
        return value == null ? BigDecimal.ZERO : value;
    }

    private int scaled(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private double round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
