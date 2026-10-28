package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.dto.TaskDtos;
import dev.xichen.backend.dto.WorkDtos;
import dev.xichen.backend.entity.Project;
import dev.xichen.backend.entity.TaskEntity;
import dev.xichen.backend.repository.ProjectRepository;
import dev.xichen.backend.repository.TaskRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class WorkService {
    private static final Set<String> PRIORITIES = Set.of("high", "mid", "low");
    private static final Set<String> AREAS = Set.of("work", "health", "life", "general");

    private final JdbcTemplate jdbc;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public WorkService(JdbcTemplate jdbc, TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.jdbc = jdbc;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public PageViews.Work overview(LocalDate requested, YearMonth month) {
        LocalDate date = requested == null ? LocalDate.now() : requested;
        YearMonth target = month == null ? YearMonth.from(date) : month;
        long userId = CurrentUser.ID;
        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1L);
        LocalDate weekEnd = weekStart.plusDays(6);

        int highPriorityOpen = count("""
                SELECT COUNT(*) FROM tasks
                WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0
                  AND priority = 'high' AND scheduled_date = ?
                """, userId, date);
        int weekDeadlines = count("""
                SELECT COUNT(*) FROM tasks
                WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0
                  AND due_at IS NOT NULL AND due_at >= ? AND due_at < ?
                """, userId, weekStart.atStartOfDay(), weekEnd.plusDays(1).atStartOfDay());

        LocalDate monthStart = target.atDay(1);
        LocalDate monthEnd = target.atEndOfMonth();

        List<PageViews.Work.CalendarEventRow> events = jdbc.query("""
                        SELECT scheduled_date, due_at, title, priority
                        FROM tasks
                        WHERE user_id = ? AND deleted_at IS NULL
                          AND ((scheduled_date BETWEEN ? AND ?) OR (due_at >= ? AND due_at < ?))
                        ORDER BY COALESCE(due_at, scheduled_date)
                        """,
                (rs, rowNum) -> {
                    LocalDate scheduled = rs.getObject("scheduled_date", LocalDate.class);
                    LocalDateTime dueAt = rs.getObject("due_at", LocalDateTime.class);
                    LocalDate day = dueAt != null ? dueAt.toLocalDate() : scheduled;
                    return new PageViews.Work.CalendarEventRow(day.getDayOfMonth(), rs.getString("title"),
                            rs.getString("priority"), dueAt != null);
                },
                userId, monthStart, monthEnd, monthStart.atStartOfDay(), monthEnd.plusDays(1).atStartOfDay());

        List<PageViews.Dashboard.TaskItem> todos = jdbc.query("""
                        SELECT id, title, area, priority, is_done, scheduled_date, due_at
                        FROM tasks
                        WHERE user_id = ? AND deleted_at IS NULL AND area = 'work' AND scheduled_date = ?
                        ORDER BY is_done ASC, FIELD(priority, 'high', 'mid', 'low'), sort_order
                        """,
                (rs, rowNum) -> new PageViews.Dashboard.TaskItem(
                        String.valueOf(rs.getLong("id")), rs.getString("title"), rs.getString("title"),
                        rs.getString("area"), rs.getString("priority"), rs.getBoolean("is_done"),
                        rs.getObject("scheduled_date", LocalDate.class).toString(),
                        rs.getObject("due_at", LocalDateTime.class) == null ? null
                                : Formats.offset(rs.getObject("due_at", LocalDateTime.class)).toString()),
                userId, date);

        List<PageViews.Work.DeadlineRow> deadlines = jdbc.query("""
                        SELECT title, due_at, priority FROM tasks
                        WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0
                          AND due_at IS NOT NULL AND due_at >= ? AND due_at < ?
                        ORDER BY due_at ASC
                        LIMIT 4
                        """,
                (rs, rowNum) -> new PageViews.Work.DeadlineRow(rs.getString("title"),
                        Formats.dueLabel(rs.getObject("due_at", LocalDateTime.class)), rs.getString("priority")),
                userId, LocalDateTime.now(), LocalDateTime.now().plusDays(30));

        List<PageViews.Work.ProjectRow> projects = projectRepository
                .findByUserIdAndStatusNotOrderBySortOrderAsc(userId, "archived").stream()
                .map(project -> new PageViews.Work.ProjectRow(String.valueOf(project.getId()), project.getName(),
                        project.getProgressPercent() == null ? 0 : project.getProgressPercent().doubleValue(),
                        project.getStage(), project.getColor()))
                .toList();

        String title = highPriorityOpen > 0 ? "本周先处理高优先级任务" : "今天的节奏比较轻";
        String desc = "今日高优 " + highPriorityOpen + " 项，本周截止 " + weekDeadlines + " 项，"
                + "在推进的项目 " + projects.size() + " 个。";

        PageViews.Work.CalendarMeta meta = new PageViews.Work.CalendarMeta(
                target.getYear(), target.getMonthValue(), date.getDayOfMonth(),
                target.getYear() + " 年 " + target.getMonthValue() + " 月",
                "今天 " + Formats.monthDay(date) + " · " + Formats.weekday(date));

        return new PageViews.Work(
                new PageViews.Work.Banner(title, desc,
                        List.of("高优 " + highPriorityOpen, "本周 DDL " + weekDeadlines, "项目 " + projects.size()),
                        highPriorityOpen, weekDeadlines),
                meta, events, todos, deadlines, projects);
    }

    // ---------- 任务 ----------

    public TaskDtos.TaskPage tasks(LocalDate date, String area, Boolean done, int page, int size) {
        long userId = CurrentUser.ID;
        StringBuilder where = new StringBuilder(" WHERE user_id = ? AND deleted_at IS NULL");
        List<Object> args = new ArrayList<>();
        args.add(userId);
        if (date != null) {
            where.append(" AND scheduled_date = ?");
            args.add(date);
        }
        if (area != null && !area.isBlank()) {
            where.append(" AND area = ?");
            args.add(area);
        }
        if (done != null) {
            where.append(" AND is_done = ?");
            args.add(done);
        }
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM tasks" + where, Integer.class, args.toArray());
        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add(size);
        pageArgs.add(Math.max(0, (page - 1) * size));
        List<TaskDtos.TaskDto> items = jdbc.query("SELECT * FROM tasks" + where
                        + " ORDER BY is_done ASC, FIELD(priority, 'high', 'mid', 'low'), sort_order LIMIT ? OFFSET ?",
                (rs, rowNum) -> toDto(rs.getLong("id"), rs.getString("title"), rs.getString("area"),
                        rs.getString("priority"), rs.getBoolean("is_done"),
                        rs.getObject("scheduled_date", LocalDate.class),
                        rs.getObject("due_at", LocalDateTime.class),
                        rs.getObject("completed_at", LocalDateTime.class),
                        rs.getInt("sort_order")),
                pageArgs.toArray());
        return new TaskDtos.TaskPage(items, total == null ? 0 : total, page, size, stats(userId, LocalDate.now()));
    }

    public TaskDtos.Stats stats(long userId, LocalDate date) {
        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1L);
        int active = count("SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0", userId);
        int dueSoon = count("""
                SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0
                  AND due_at IS NOT NULL AND due_at >= ? AND due_at < ?
                """, userId, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        int highOpen = count("""
                SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND is_done = 0 AND priority = 'high'
                """, userId);
        int doneThisWeek = count("""
                SELECT COUNT(*) FROM tasks WHERE user_id = ? AND deleted_at IS NULL AND is_done = 1
                  AND completed_at >= ? AND completed_at < ?
                """, userId, weekStart.atStartOfDay(), weekStart.plusDays(7).atStartOfDay());
        return new TaskDtos.Stats(active, dueSoon, highOpen, doneThisWeek);
    }

    private TaskDtos.TaskDto toDto(long id, String title, String area, String priority, boolean done,
                                   LocalDate scheduledDate, LocalDateTime dueAt, LocalDateTime completedAt, int sortOrder) {
        return new TaskDtos.TaskDto(String.valueOf(id), title, title, area, priority, done,
                scheduledDate == null ? null : scheduledDate.toString(),
                dueAt == null ? null : Formats.offset(dueAt).toString(),
                completedAt == null ? null : Formats.offset(completedAt).toString(),
                sortOrder, null, null);
    }

    @Transactional
    public TaskDtos.TaskDto createTask(TaskDtos.CreateRequest request) {
        long userId = CurrentUser.ID;
        String area = request.area() == null ? "general" : request.area();
        String priority = request.priority() == null ? "mid" : request.priority();
        validateTask(area, priority);
        if (request.dueAt() != null && request.dueAt().isBefore(LocalDateTime.now())) {
            throw ApiException.badRequest("截止时间必须晚于当前时间");
        }
        TaskEntity task = new TaskEntity();
        task.setUserId(userId);
        task.setTitle(request.title());
        task.setNote(request.note());
        task.setArea(area);
        task.setPriority(priority);
        task.setDone(false);
        task.setScheduledDate(request.scheduledDate() == null ? LocalDate.now() : request.scheduledDate());
        task.setDueAt(request.dueAt());
        task.setSortOrder(nextSortOrder(userId));
        TaskEntity saved = taskRepository.save(task);
        return toDto(saved.getId(), saved.getTitle(), saved.getArea(), saved.getPriority(), Boolean.TRUE.equals(saved.getDone()),
                saved.getScheduledDate(), saved.getDueAt(), saved.getCompletedAt(), saved.getSortOrder());
    }

    @Transactional
    public TaskDtos.TaskDto patchTask(Long id, TaskDtos.PatchRequest request) {
        long userId = CurrentUser.ID;
        TaskEntity task = taskRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> ApiException.notFound("任务不存在"));
        if (request.area() != null) {
            validateTask(request.area(), request.priority() == null ? task.getPriority() : request.priority());
            task.setArea(request.area());
        }
        if (request.priority() != null) {
            validateTask(task.getArea(), request.priority());
            task.setPriority(request.priority());
        }
        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.note() != null) {
            task.setNote(request.note());
        }
        if (request.scheduledDate() != null) {
            task.setScheduledDate(request.scheduledDate());
        }
        if (request.dueAt() != null) {
            task.setDueAt(request.dueAt());
        }
        if (request.done() != null) {
            task.setDone(request.done());
            task.setCompletedAt(request.done() ? LocalDateTime.now() : null);
        }
        TaskEntity saved = taskRepository.save(task);
        return toDto(saved.getId(), saved.getTitle(), saved.getArea(), saved.getPriority(), Boolean.TRUE.equals(saved.getDone()),
                saved.getScheduledDate(), saved.getDueAt(), saved.getCompletedAt(), saved.getSortOrder());
    }

    @Transactional
    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findByIdAndUserIdAndDeletedAtIsNull(id, CurrentUser.ID)
                .orElseThrow(() -> ApiException.notFound("任务不存在"));
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    private void validateTask(String area, String priority) {
        if (!AREAS.contains(area)) {
            throw ApiException.badRequest("area 只能是 work / health / life / general");
        }
        if (!PRIORITIES.contains(priority)) {
            throw ApiException.badRequest("priority 只能是 high / mid / low");
        }
    }

    private int nextSortOrder(long userId) {
        Integer max = jdbc.queryForObject("SELECT COALESCE(MAX(sort_order), -1) FROM tasks WHERE user_id = ?", Integer.class, userId);
        return max == null ? 0 : max + 1;
    }

    // ---------- 项目 ----------

    @Transactional
    public WorkDtos.ProjectDto createProject(WorkDtos.ProjectRequest request) {
        Project project = new Project();
        project.setUserId(CurrentUser.ID);
        project.setName(request.name());
        project.setStage(request.stage());
        project.setProgressPercent(request.progressPercent() == null ? BigDecimal.ZERO : request.progressPercent());
        project.setStatus(request.status() == null ? "active" : request.status());
        project.setStartDate(request.startDate());
        project.setTargetDate(request.targetDate());
        project.setColor(request.color() == null ? "green" : request.color());
        Integer max = jdbc.queryForObject("SELECT COALESCE(MAX(sort_order), -1) FROM projects WHERE user_id = ?", Integer.class, CurrentUser.ID);
        project.setSortOrder(max == null ? 0 : max + 1);
        return toProjectDto(projectRepository.save(project));
    }

    @Transactional
    public WorkDtos.ProjectDto updateProject(Long id, WorkDtos.ProjectRequest request) {
        Project project = projectRepository.findByIdAndUserId(id, CurrentUser.ID)
                .orElseThrow(() -> ApiException.notFound("项目不存在"));
        if (request.name() != null) {
            project.setName(request.name());
        }
        if (request.stage() != null) {
            project.setStage(request.stage());
        }
        if (request.progressPercent() != null) {
            project.setProgressPercent(request.progressPercent());
        }
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        if (request.startDate() != null) {
            project.setStartDate(request.startDate());
        }
        if (request.targetDate() != null) {
            project.setTargetDate(request.targetDate());
        }
        if (request.color() != null) {
            project.setColor(request.color());
        }
        return toProjectDto(projectRepository.save(project));
    }

    private WorkDtos.ProjectDto toProjectDto(Project project) {
        return new WorkDtos.ProjectDto(String.valueOf(project.getId()), project.getName(), project.getStage(),
                project.getProgressPercent() == null ? 0 : project.getProgressPercent().doubleValue(),
                project.getStatus(), project.getStartDate(), project.getTargetDate(), project.getColor());
    }

    private int count(String sql, Object... args) {
        Integer value = jdbc.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }
}
