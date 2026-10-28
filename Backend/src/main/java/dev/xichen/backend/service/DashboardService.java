package dev.xichen.backend.service;

import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.common.Styles;
import dev.xichen.backend.common.Views;
import dev.xichen.backend.entity.AnnualGoal;
import dev.xichen.backend.entity.User;
import dev.xichen.backend.repository.AnnualGoalRepository;
import dev.xichen.backend.repository.UserRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final JdbcTemplate jdbc;
    private final UserRepository userRepository;
    private final AnnualGoalRepository annualGoalRepository;
    private final NotesIndexService notesIndexService;

    public DashboardService(JdbcTemplate jdbc, UserRepository userRepository,
                            AnnualGoalRepository annualGoalRepository, NotesIndexService notesIndexService) {
        this.jdbc = jdbc;
        this.userRepository = userRepository;
        this.annualGoalRepository = annualGoalRepository;
        this.notesIndexService = notesIndexService;
    }

    public PageViews.Dashboard overview(LocalDate requested) {
        LocalDate date = requested == null ? LocalDate.now() : requested;
        User user = currentUser();
        long userId = CurrentUser.ID;

        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1L);
        LocalDate weekEnd = weekStart.plusDays(6);

        int weekTotal = count("SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND scheduled_date BETWEEN ? AND ?", userId, weekStart, weekEnd);
        int weekDone = count("SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND is_done = 1 AND scheduled_date BETWEEN ? AND ?", userId, weekStart, weekEnd);
        int completionRate = Formats.percent(weekDone, weekTotal);

        int streak = user.getCurrentStreakDays() == null ? 0 : user.getCurrentStreakDays();

        List<PageViews.Dashboard.TaskItem> todayTasks = todayTasks(userId, date);
        List<Views.Metric> metrics = metrics(userId, date);

        return new PageViews.Dashboard(
                date.toString(),
                new PageViews.Dashboard.Hero(
                        user.getDisplayName(),
                        streak,
                        completionRate,
                        user.getTargetYear(),
                        greeting(user.getDisplayName()),
                        user.getSignature(),
                        List.of("已连续记录 " + streak + " 天", "本周完成度 " + completionRate + "%", "目标 " + user.getTargetYear()),
                        String.format("%02d / %02d", date.getMonthValue(), date.getDayOfMonth()),
                        Formats.weekday(date) + " · " + Formats.monthName(date.getMonthValue())),
                metrics,
                yearGoals(userId, user.getTargetYear()),
                cashTrend(userId, date),
                todayTasks,
                expenseStructure(userId, date),
                mediaProgress(userId),
                recentNotes());
    }

    private User currentUser() {
        return userRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> dev.xichen.backend.common.ApiException.notFound("用户不存在"));
    }

    private String greeting(String name) {
        int hour = LocalTime.now().getHour();
        String prefix;
        if (hour < 6) {
            prefix = "夜深了";
        } else if (hour < 11) {
            prefix = "早安";
        } else if (hour < 14) {
            prefix = "中午好";
        } else if (hour < 18) {
            prefix = "下午好";
        } else if (hour < 23) {
            prefix = "晚安";
        } else {
            prefix = "夜深了";
        }
        return prefix.equals("夜深了") ? prefix + "，" + name : prefix + "，" + name;
    }

    private List<Views.Metric> metrics(long userId, LocalDate date) {
        List<Views.Metric> metrics = new ArrayList<>();

        List<Map<String, Object>> sleepRows = jdbc.queryForList(
                "SELECT duration_minutes, deep_minutes FROM sleep_records WHERE user_id = ? ORDER BY sleep_date DESC LIMIT 1", userId);
        int duration = sleepRows.isEmpty() ? 0 : ((Number) sleepRows.get(0).get("duration_minutes")).intValue();
        Double previousAvg = jdbc.queryForObject(
                "SELECT AVG(duration_minutes) FROM sleep_records WHERE user_id = ? AND sleep_date BETWEEN ? AND ?",
                Double.class, userId, date.minusDays(13), date.minusDays(7));
        double diff = previousAvg == null ? 0 : (duration - previousAvg) / 60.0;
        metrics.add(new Views.Metric("sleep", "睡眠",
                sleepRows.isEmpty() ? "—" : Formats.durationHm(duration),
                sleepRows.isEmpty() ? null : diff >= 0 ? "up" : "down",
                sleepRows.isEmpty() ? null : (diff >= 0 ? "+" : "") + Formats.oneDecimal(diff) + "h",
                sleepRows.isEmpty() ? "暂无记录" : "较上周"));

        BigDecimal monthExpense = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?",
                userId, monthStart(date), monthStart(date).plusMonths(1));
        BigDecimal prevExpense = sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND deleted_at IS NULL AND type = 'expense' AND occurred_at >= ? AND occurred_at < ?",
                userId, monthStart(date).minusMonths(1), monthStart(date));
        double change = prevExpense.signum() == 0 ? 0
                : monthExpense.subtract(prevExpense).doubleValue() / prevExpense.doubleValue() * 100;
        metrics.add(new Views.Metric("expense", "本月支出", Formats.money(monthExpense),
                change <= 0 ? "down" : "up",
                String.format("%.1f%%", Math.abs(change) == 0 ? 0.0 : change),
                "较上月"));

        List<Map<String, Object>> activityRows = jdbc.queryForList(
                "SELECT steps, step_goal FROM daily_activities WHERE user_id = ? AND activity_date = ?", userId, date);
        int steps = activityRows.isEmpty() ? 0 : ((Number) activityRows.get(0).get("steps")).intValue();
        int goal = activityRows.isEmpty() ? 10000 : ((Number) activityRows.get(0).get("step_goal")).intValue();
        metrics.add(new Views.Metric("steps", "今日步数", Formats.number(steps), null, null,
                "目标 " + Formats.number(goal) + " · " + Formats.percent(steps, goal) + "%"));

        int streak = currentUser().getCurrentStreakDays() == null ? 0 : currentUser().getCurrentStreakDays();
        boolean activeToday = date.equals(currentUser().getLastActiveOn());
        metrics.add(new Views.Metric("streak", "连续记录", streak + " 天", "up", "+1",
                activeToday ? "今日已记录" : "尚未记录"));
        return metrics;
    }

    private List<PageViews.Dashboard.TaskItem> todayTasks(long userId, LocalDate date) {
        return jdbc.query("""
                        SELECT id, title, area, priority, is_done, scheduled_date, due_at
                        FROM tasks
                        WHERE user_id = ? AND deleted_at IS NULL AND scheduled_date = ?
                        ORDER BY is_done DESC, FIELD(priority, 'high', 'mid', 'low'), sort_order
                        """,
                (rs, rowNum) -> new PageViews.Dashboard.TaskItem(
                        String.valueOf(rs.getLong("id")),
                        rs.getString("title"),
                        rs.getString("title"),
                        rs.getString("area"),
                        rs.getString("priority"),
                        rs.getBoolean("is_done"),
                        rs.getObject("scheduled_date", LocalDate.class).toString(),
                        Formats.offset(rs.getObject("due_at", LocalDateTime.class)) == null ? null
                                : Formats.offset(rs.getObject("due_at", LocalDateTime.class)).toString()),
                userId, date);
    }

    private List<Views.Goal> yearGoals(long userId, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = from.plusYears(1);
        List<Views.Goal> goals = new ArrayList<>();
        for (AnnualGoal goal : annualGoalRepository.findByUserIdAndYearOrderBySortOrderAsc(userId, year)) {
            double current = switch (goal.getMetricKey()) {
                case "books_finished" -> count("SELECT COUNT(*) FROM media_items WHERE user_id = ? AND media_type = 'book' AND status = 'finished' AND deleted_at IS NULL AND finished_on >= ? AND finished_on < ?", userId, from, to);
                case "running_km" -> sum("SELECT COALESCE(SUM(distance_km), 0) FROM workouts WHERE user_id = ? AND workout_type = 'run' AND started_at >= ? AND started_at < ?", userId, from.atStartOfDay(), to.atStartOfDay()).doubleValue();
                case "savings_amount" -> income(userId, from, to).subtract(expense(userId, from, to)).doubleValue();
                case "travel_count" -> count("SELECT COUNT(*) FROM life_activities WHERE user_id = ? AND activity_type = 'travel' AND occurred_at >= ? AND occurred_at < ?", userId, from.atStartOfDay(), to.atStartOfDay());
                default -> 0;
            };
            BigDecimal target = goal.getTargetValue();
            String valueText = switch (goal.getUnit()) {
                case "CNY" -> Formats.money(BigDecimal.valueOf(current)) + " / " + Formats.money(target);
                case "km" -> Math.round(current) + " / " + Formats.oneDecimal(target).replace(".0", "") + " km";
                case "次" -> Math.round(current) + " / " + target.setScale(0, RoundingMode.HALF_UP) + " 次";
                default -> Math.round(current) + " / " + target.setScale(0, RoundingMode.HALF_UP);
            };
            double percent = Formats.percentRaw(current, target.doubleValue());
            goals.add(new Views.Goal(goal.getName(), percent, valueText, Math.round(percent) + "%", goal.getColor()));
        }
        return goals;
    }

    private PageViews.Dashboard.CashTrend cashTrend(long userId, LocalDate date) {
        LocalDate first = monthStart(date).minusMonths(5);
        LocalDate end = monthStart(date).plusMonths(1);
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT DATE_FORMAT(occurred_at, '%Y-%m') AS ym, type, SUM(amount) AS total
                        FROM transactions
                        WHERE user_id = ? AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?
                        GROUP BY ym, type
                        """, userId, first.atStartOfDay(), end.atStartOfDay());
        Map<String, Integer> incomeMap = new HashMap<>();
        Map<String, Integer> expenseMap = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String key = (String) row.get("ym");
            int total = ((BigDecimal) row.get("total")).setScale(0, RoundingMode.HALF_UP).intValue();
            if ("income".equals(row.get("type"))) {
                incomeMap.put(key, total);
            } else {
                expenseMap.put(key, total);
            }
        }
        List<String> labels = new ArrayList<>();
        List<Integer> income = new ArrayList<>();
        List<Integer> expense = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            LocalDate month = first.plusMonths(i);
            String key = String.format("%04d-%02d", month.getYear(), month.getMonthValue());
            labels.add(Formats.monthShort(month.getMonthValue()));
            income.add(incomeMap.getOrDefault(key, 0));
            expense.add(expenseMap.getOrDefault(key, 0));
        }
        return new PageViews.Dashboard.CashTrend(labels, income, expense);
    }

    private List<Views.ProgressItem> expenseStructure(long userId, LocalDate date) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT c.name, c.color, SUM(t.amount) AS total
                        FROM transactions t
                        JOIN finance_categories c ON c.id = t.category_id
                        WHERE t.user_id = ? AND t.type = 'expense' AND t.deleted_at IS NULL
                          AND t.occurred_at >= ? AND t.occurred_at < ?
                        GROUP BY c.id, c.name, c.color
                        ORDER BY total DESC
                        LIMIT 4
                        """, userId, monthStart(date).atStartOfDay(), monthStart(date).plusMonths(1).atStartOfDay());
        List<Views.ProgressItem> items = new ArrayList<>();
        double top = rows.isEmpty() ? 0 : ((BigDecimal) rows.get(0).get("total")).doubleValue();
        for (Map<String, Object> row : rows) {
            BigDecimal total = (BigDecimal) row.get("total");
            items.add(new Views.ProgressItem((String) row.get("name"), Formats.money(total),
                    Formats.percent(total.doubleValue(), top), Styles.barColor((String) row.get("color"))));
        }
        return items;
    }

    private List<Views.ProgressItem> mediaProgress(long userId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                        SELECT id, title, progress_current, progress_total, progress_unit
                        FROM media_items
                        WHERE user_id = ? AND deleted_at IS NULL AND status = 'in_progress'
                        ORDER BY COALESCE(last_activity_at, updated_at) DESC
                        LIMIT 4
                        """, userId);
        List<Views.ProgressItem> items = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            double current = row.get("progress_current") == null ? 0 : ((BigDecimal) row.get("progress_current")).doubleValue();
            double total = row.get("progress_total") == null ? 0 : ((BigDecimal) row.get("progress_total")).doubleValue();
            String unit = (String) row.get("progress_unit");
            String valueText = "episode".equals(unit)
                    ? Math.round(current) + " / " + Math.round(total) + " 集"
                    : Math.round(current) + "%";
            items.add(new Views.ProgressItem("《" + row.get("title").toString().replace("《", "").replace("》", "") + "》",
                    valueText, Formats.percent(current, total), Styles.rotateBar(i)));
        }
        return items;
    }

    private List<PageViews.Dashboard.RecentNote> recentNotes() {
        return notesIndexService.overview().recentNotes().stream()
                .limit(4)
                .map(note -> new PageViews.Dashboard.RecentNote(
                        note.path(), note.path(), note.title(),
                        note.tag().replace("#", ""), note.timeAgo(), note.updatedAt()))
                .toList();
    }

    // ---------- SQL helpers ----------

    private LocalDate monthStart(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    private int count(String sql, Object... args) {
        Integer value = jdbc.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    private BigDecimal sum(String sql, Object... args) {
        BigDecimal value = jdbc.queryForObject(sql, BigDecimal.class, args);
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal income(long userId, LocalDate from, LocalDate to) {
        return sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'income' AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?",
                userId, from.atStartOfDay(), to.atStartOfDay());
    }

    private BigDecimal expense(long userId, LocalDate from, LocalDate to) {
        return sum("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'expense' AND deleted_at IS NULL AND occurred_at >= ? AND occurred_at < ?",
                userId, from.atStartOfDay(), to.atStartOfDay());
    }
}
