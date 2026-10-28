package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.common.Styles;
import dev.xichen.backend.common.Views;
import dev.xichen.backend.dto.LifeDtos;
import dev.xichen.backend.entity.LifeActivity;
import dev.xichen.backend.entity.MediaItem;
import dev.xichen.backend.repository.LifeActivityRepository;
import dev.xichen.backend.repository.MediaItemRepository;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LifeService {
    private static final Set<String> ACTIVITY_TYPES = Set.of("sport", "solitude", "gathering", "travel", "other");
    private static final Set<String> MEDIA_TYPES = Set.of("book", "movie", "series", "documentary", "anime");
    private static final Set<String> MEDIA_STATUSES = Set.of("in_progress", "finished", "wishlist", "paused");

    private final JdbcTemplate jdbc;
    private final LifeActivityRepository activityRepository;
    private final MediaItemRepository mediaItemRepository;

    public LifeService(JdbcTemplate jdbc, LifeActivityRepository activityRepository,
                       MediaItemRepository mediaItemRepository) {
        this.jdbc = jdbc;
        this.activityRepository = activityRepository;
        this.mediaItemRepository = mediaItemRepository;
    }

    public PageViews.Life overview(LocalDate requested, YearMonth requestedMonth) {
        YearMonth month = requestedMonth == null ? YearMonth.from(requested == null ? LocalDate.now() : requested) : requestedMonth;
        long userId = CurrentUser.ID;
        LocalDateTime from = month.atDay(1).atStartOfDay();
        LocalDateTime to = month.plusMonths(1).atDay(1).atStartOfDay();

        int activityCount = count("SELECT COUNT(*) FROM life_activities WHERE user_id = ? AND occurred_at >= ? AND occurred_at < ?", userId, from, to);
        int booksFinished = count("""
                SELECT COUNT(*) FROM media_items
                WHERE user_id = ? AND deleted_at IS NULL AND media_type = 'book' AND status = 'finished'
                  AND finished_on >= ? AND finished_on < ?
                """, userId, month.atDay(1), month.plusMonths(1).atDay(1));
        int mediaFinished = count("""
                SELECT COUNT(*) FROM media_items
                WHERE user_id = ? AND deleted_at IS NULL AND media_type <> 'book' AND status = 'finished'
                  AND finished_on >= ? AND finished_on < ?
                """, userId, month.atDay(1), month.plusMonths(1).atDay(1));
        int travelCount = count("""
                SELECT COUNT(*) FROM life_activities
                WHERE user_id = ? AND activity_type = 'travel' AND occurred_at >= ? AND occurred_at < ?
                """, userId, from, to);

        List<Map<String, Object>> activityRows = jdbc.queryForList("""
                        SELECT activity_type, COUNT(*) AS amount
                        FROM life_activities
                        WHERE user_id = ? AND occurred_at >= ? AND occurred_at < ?
                        GROUP BY activity_type
                        ORDER BY amount DESC
                        """, userId, from, to);
        List<PageViews.Life.Slice> activityDist = new ArrayList<>();
        for (Map<String, Object> row : activityRows) {
            activityDist.add(new PageViews.Life.Slice(activityLabel((String) row.get("activity_type")),
                    ((Number) row.get("amount")).intValue()));
        }

        List<Map<String, Object>> mediaRows = jdbc.queryForList("""
                        SELECT media_type, COUNT(*) AS amount
                        FROM media_items
                        WHERE user_id = ? AND deleted_at IS NULL AND media_type <> 'book'
                        GROUP BY media_type
                        ORDER BY amount DESC
                        """, userId);
        List<PageViews.Life.Slice> mediaDist = new ArrayList<>();
        for (Map<String, Object> row : mediaRows) {
            mediaDist.add(new PageViews.Life.Slice(Styles.mediaLabel((String) row.get("media_type")),
                    ((Number) row.get("amount")).intValue()));
        }

        List<PageViews.Life.MediaRow> finished = jdbc.query("""
                        SELECT id, media_type, title, creator, rating, finished_on
                        FROM media_items
                        WHERE user_id = ? AND deleted_at IS NULL AND status = 'finished'
                        ORDER BY finished_on DESC
                        LIMIT 6
                        """,
                (rs, rowNum) -> {
                    String type = rs.getString("media_type");
                    LocalDate finishedOn = rs.getObject("finished_on", LocalDate.class);
                    String meta = finishedOn == null ? Styles.mediaLabel(type) : Formats.monthDay(finishedOn);
                    return new PageViews.Life.MediaRow(
                            String.valueOf(rs.getLong("id")),
                            Styles.mediaLabel(type),
                            Styles.mediaPill(type),
                            rs.getString("title"),
                            meta,
                            rs.getObject("rating") == null ? 0 : rs.getInt("rating"));
                },
                userId);

        String title = mediaFinished + booksFinished > 0 ? "本月输入状态平稳" : "这个月还没有完成的作品";
        String desc = "本月读完 " + booksFinished + " 本、看完 " + mediaFinished + " 部，记录活动 "
                + activityCount + " 次，旅行 " + travelCount + " 次。";

        return new PageViews.Life(
                new PageViews.Life.Banner(title, desc,
                        List.of(month.getMonthValue() + " 月", "活动 " + activityCount + " 次", "阅读 " + booksFinished + " 本"),
                        activityCount, booksFinished, mediaFinished),
                List.of(
                        new Views.Metric("books", "本月阅读", String.valueOf(booksFinished), null, null, "已读完"),
                        new Views.Metric("media", "本月观影", String.valueOf(mediaFinished), null, null, "已看完"),
                        new Views.Metric("activities", "本月活动", String.valueOf(activityCount), null, null, "次记录"),
                        new Views.Metric("travel", "本月旅行", String.valueOf(travelCount), null, null, "次出行")),
                activityDist,
                mediaDist,
                readingProgress(userId),
                watchingProgress(userId),
                finished);
    }

    private List<Views.ProgressItem> readingProgress(long userId) {
        return jdbc.query("""
                        SELECT id, title, progress_current, progress_total, progress_unit
                        FROM media_items
                        WHERE user_id = ? AND deleted_at IS NULL AND media_type = 'book' AND status = 'in_progress'
                        ORDER BY COALESCE(last_activity_at, updated_at) DESC
                        """,
                (rs, rowNum) -> {
                    double current = rs.getObject("progress_current") == null ? 0 : rs.getBigDecimal("progress_current").doubleValue();
                    double total = rs.getObject("progress_total") == null ? 0 : rs.getBigDecimal("progress_total").doubleValue();
                    return new Views.ProgressItem(rs.getString("title"), Math.round(current) + "%",
                            Formats.percent(current, total), Styles.rotateBar(rowNum));
                },
                userId);
    }

    private List<Views.ProgressItem> watchingProgress(long userId) {
        return jdbc.query("""
                        SELECT id, title, progress_current, progress_total, progress_unit
                        FROM media_items
                        WHERE user_id = ? AND deleted_at IS NULL AND media_type <> 'book' AND status = 'in_progress'
                        ORDER BY COALESCE(last_activity_at, updated_at) DESC
                        """,
                (rs, rowNum) -> {
                    double current = rs.getObject("progress_current") == null ? 0 : rs.getBigDecimal("progress_current").doubleValue();
                    double total = rs.getObject("progress_total") == null ? 0 : rs.getBigDecimal("progress_total").doubleValue();
                    String unit = rs.getString("progress_unit");
                    String valueText = "episode".equals(unit)
                            ? Math.round(current) + " / " + Math.round(total) + " 集"
                            : Math.round(current) + "%";
                    int percent = "percent".equals(unit) ? (int) Math.round(current) : Formats.percent(current, total);
                    return new Views.ProgressItem(rs.getString("title"), valueText, percent, Styles.rotateBar(rowNum));
                },
                userId);
    }

    // ---------- 活动 ----------

    public List<LifeDtos.ActivityDto> activities(Integer year, Integer month) {
        YearMonth target = (year == null || month == null) ? YearMonth.now() : YearMonth.of(year, month);
        return activityRepository.findByUserIdAndOccurredAtBetweenOrderByOccurredAtDesc(CurrentUser.ID,
                        target.atDay(1).atStartOfDay(), target.plusMonths(1).atDay(1).atStartOfDay())
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public LifeDtos.ActivityDto createActivity(LifeDtos.ActivityRequest request) {
        if (!ACTIVITY_TYPES.contains(request.activityType())) {
            throw ApiException.badRequest("activityType 不合法");
        }
        LifeActivity activity = new LifeActivity();
        activity.setUserId(CurrentUser.ID);
        activity.setActivityType(request.activityType());
        activity.setTitle(request.title());
        activity.setOccurredAt(request.occurredAt());
        activity.setDurationMinutes(request.durationMinutes());
        activity.setNote(request.note());
        return toDto(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Long id) {
        LifeActivity activity = activityRepository.findByIdAndUserId(id, CurrentUser.ID)
                .orElseThrow(() -> ApiException.notFound("活动不存在"));
        activityRepository.delete(activity);
    }

    private LifeDtos.ActivityDto toDto(LifeActivity activity) {
        return new LifeDtos.ActivityDto(String.valueOf(activity.getId()), activity.getActivityType(),
                activity.getTitle(), Formats.offset(activity.getOccurredAt()).toString(),
                activity.getDurationMinutes(), activity.getNote());
    }

    // ---------- 媒体 ----------

    public List<LifeDtos.MediaDto> media(String mediaType, String status) {
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM media_items WHERE user_id = ? AND deleted_at IS NULL
                """);
        List<Object> args = new ArrayList<>();
        args.add(CurrentUser.ID);
        if (mediaType != null && !mediaType.isBlank()) {
            sql.append(" AND media_type = ?");
            args.add(mediaType);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            args.add(status);
        }
        sql.append(" ORDER BY COALESCE(last_activity_at, updated_at) DESC");
        return jdbc.query(sql.toString(),
                (rs, rowNum) -> new LifeDtos.MediaDto(
                        String.valueOf(rs.getLong("id")),
                        rs.getString("media_type"),
                        rs.getString("title"),
                        rs.getString("subtitle"),
                        rs.getString("creator"),
                        rs.getString("status"),
                        rs.getObject("progress_current") == null ? null : Formats.plain(rs.getBigDecimal("progress_current")),
                        rs.getObject("progress_total") == null ? null : Formats.plain(rs.getBigDecimal("progress_total")),
                        rs.getString("progress_unit"),
                        mediaPercent(rs.getBigDecimal("progress_current"), rs.getBigDecimal("progress_total"), rs.getString("progress_unit")),
                        rs.getObject("rating") == null ? null : rs.getInt("rating"),
                        rs.getObject("started_on", LocalDate.class) == null ? null : rs.getObject("started_on", LocalDate.class).toString(),
                        rs.getObject("finished_on", LocalDate.class) == null ? null : rs.getObject("finished_on", LocalDate.class).toString()),
                args.toArray());
    }

    @Transactional
    public LifeDtos.MediaDto createMedia(LifeDtos.MediaRequest request) {
        validateMedia(request.mediaType(), request.status());
        MediaItem item = new MediaItem();
        item.setUserId(CurrentUser.ID);
        item.setMediaType(request.mediaType());
        item.setTitle(request.title());
        item.setSubtitle(request.subtitle());
        item.setCreator(request.creator());
        item.setStatus(request.status() == null ? "in_progress" : request.status());
        item.setProgressCurrent(request.progressCurrent());
        item.setProgressTotal(request.progressTotal());
        item.setProgressUnit(request.progressUnit() == null ? "percent" : request.progressUnit());
        item.setRating(request.rating());
        item.setStartedOn(request.startedOn());
        item.setFinishedOn(request.finishedOn());
        item.setLastActivityAt(LocalDateTime.now());
        MediaItem saved = mediaItemRepository.save(item);
        return toMediaDto(saved);
    }

    @Transactional
    public LifeDtos.MediaDto patchMedia(Long id, LifeDtos.MediaPatch patch) {
        MediaItem item = mediaItemRepository.findByIdAndUserIdAndDeletedAtIsNull(id, CurrentUser.ID)
                .orElseThrow(() -> ApiException.notFound("条目不存在"));
        if (patch.title() != null) {
            item.setTitle(patch.title());
        }
        if (patch.subtitle() != null) {
            item.setSubtitle(patch.subtitle());
        }
        if (patch.status() != null) {
            validateMedia(item.getMediaType(), patch.status());
            item.setStatus(patch.status());
            if ("finished".equals(patch.status())) {
                if (item.getFinishedOn() == null) {
                    item.setFinishedOn(LocalDate.now());
                }
                if (item.getProgressTotal() != null) {
                    item.setProgressCurrent(item.getProgressTotal());
                }
            }
        }
        if (patch.progressCurrent() != null) {
            item.setProgressCurrent(patch.progressCurrent());
        }
        if (patch.progressTotal() != null) {
            item.setProgressTotal(patch.progressTotal());
        }
        if (patch.progressUnit() != null) {
            item.setProgressUnit(patch.progressUnit());
        }
        if (patch.rating() != null) {
            item.setRating(patch.rating());
        }
        if (patch.startedOn() != null) {
            item.setStartedOn(patch.startedOn());
        }
        if (patch.finishedOn() != null) {
            item.setFinishedOn(patch.finishedOn());
        }
        if ("finished".equals(item.getStatus()) && item.getFinishedOn() == null) {
            throw ApiException.badRequest("finished 状态必须有 finished_on");
        }
        item.setLastActivityAt(LocalDateTime.now());
        return toMediaDto(mediaItemRepository.save(item));
    }

    @Transactional
    public void deleteMedia(Long id) {
        MediaItem item = mediaItemRepository.findByIdAndUserIdAndDeletedAtIsNull(id, CurrentUser.ID)
                .orElseThrow(() -> ApiException.notFound("条目不存在"));
        item.setDeletedAt(LocalDateTime.now());
        mediaItemRepository.save(item);
    }

    private LifeDtos.MediaDto toMediaDto(MediaItem item) {
        return new LifeDtos.MediaDto(String.valueOf(item.getId()), item.getMediaType(), item.getTitle(),
                item.getSubtitle(), item.getCreator(), item.getStatus(),
                item.getProgressCurrent() == null ? null : Formats.plain(item.getProgressCurrent()),
                item.getProgressTotal() == null ? null : Formats.plain(item.getProgressTotal()),
                item.getProgressUnit(),
                mediaPercent(item.getProgressCurrent(), item.getProgressTotal(), item.getProgressUnit()),
                item.getRating(),
                item.getStartedOn() == null ? null : item.getStartedOn().toString(),
                item.getFinishedOn() == null ? null : item.getFinishedOn().toString());
    }

    private int mediaPercent(BigDecimal current, BigDecimal total, String unit) {
        if (current == null) {
            return 0;
        }
        if ("percent".equals(unit)) {
            return current.setScale(0, RoundingMode.HALF_UP).intValue();
        }
        if (total == null || total.signum() <= 0) {
            return 0;
        }
        return Formats.percent(current.doubleValue(), total.doubleValue());
    }

    private void validateMedia(String mediaType, String status) {
        if (!MEDIA_TYPES.contains(mediaType)) {
            throw ApiException.badRequest("mediaType 不合法");
        }
        if (status != null && !MEDIA_STATUSES.contains(status)) {
            throw ApiException.badRequest("status 不合法");
        }
    }

    private String activityLabel(String type) {
        if (type == null) {
            return "其他";
        }
        return switch (type) {
            case "sport" -> "运动";
            case "solitude" -> "独处";
            case "gathering" -> "聚会";
            case "travel" -> "旅行";
            default -> "其他";
        };
    }

    private int count(String sql, Object... args) {
        Integer value = jdbc.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }
}
