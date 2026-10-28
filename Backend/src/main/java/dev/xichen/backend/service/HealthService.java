package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.dto.HealthDtos;
import dev.xichen.backend.entity.BodyMetric;
import dev.xichen.backend.entity.DailyActivity;
import dev.xichen.backend.entity.Habit;
import dev.xichen.backend.entity.HabitLog;
import dev.xichen.backend.entity.SleepRecord;
import dev.xichen.backend.entity.Workout;
import dev.xichen.backend.repository.BodyMetricRepository;
import dev.xichen.backend.repository.DailyActivityRepository;
import dev.xichen.backend.repository.HabitLogRepository;
import dev.xichen.backend.repository.HabitRepository;
import dev.xichen.backend.repository.SleepRecordRepository;
import dev.xichen.backend.repository.WorkoutRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HealthService {
    private final SleepRecordRepository sleepRepository;
    private final DailyActivityRepository activityRepository;
    private final WorkoutRepository workoutRepository;
    private final BodyMetricRepository bodyMetricRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    public HealthService(SleepRecordRepository sleepRepository, DailyActivityRepository activityRepository,
                         WorkoutRepository workoutRepository, BodyMetricRepository bodyMetricRepository,
                         HabitRepository habitRepository, HabitLogRepository habitLogRepository) {
        this.sleepRepository = sleepRepository;
        this.activityRepository = activityRepository;
        this.workoutRepository = workoutRepository;
        this.bodyMetricRepository = bodyMetricRepository;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
    }

    public PageViews.Health overview(LocalDate requested) {
        LocalDate date = requested == null ? LocalDate.now() : requested;
        long userId = CurrentUser.ID;
        LocalDate weekStart = weekStart(date);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<SleepRecord> weekSleep = sleepRepository
                .findByUserIdAndSleepDateBetweenOrderBySleepDateDesc(userId, weekStart, weekEnd);
        Map<LocalDate, SleepRecord> sleepByDate = weekSleep.stream()
                .collect(Collectors.toMap(SleepRecord::getSleepDate, record -> record, (a, b) -> a));

        SleepRecord latest = sleepRepository.findByUserIdAndSleepDateBetweenOrderBySleepDateDesc(
                        userId, date.minusDays(1), date)
                .stream().findFirst()
                .orElseGet(() -> sleepRepository.findTopByUserIdOrderBySleepDateDesc(userId).orElse(null));

        DailyActivity activity = activityRepository.findByUserIdAndActivityDate(userId, date).orElse(null);
        int steps = activity == null ? 0 : activity.getSteps();
        int stepGoal = activity == null ? 10000 : activity.getStepGoal();

        BigDecimal weight = bodyMetricRepository
                .findByUserIdAndMeasuredOnBetweenOrderByMeasuredOnDesc(userId, date.minusDays(30), date)
                .stream().findFirst().map(BodyMetric::getWeightKg).orElse(null);

        int weekGoalDays = weekGoalDays(userId, weekStart, weekEnd, date);

        PageViews.Health.Hero hero = new PageViews.Health.Hero(
                latest == null ? "身体数据待补充" : "身体状态稳定",
                buildDesc(latest, steps, stepGoal, weekGoalDays),
                List.of("本周达标 " + weekGoalDays + " 天", "步数 " + Formats.number(steps), "习惯 4 项"),
                latest == null ? null : latest.getDurationMinutes(),
                latest == null ? null : deepRatio(latest),
                steps,
                weight == null ? null : weight.doubleValue());

        List<PageViews.Health.Mini> mini = new ArrayList<>();
        mini.add(new PageViews.Health.Mini("睡眠",
                latest == null ? "—" : Formats.durationHm(latest.getDurationMinutes()),
                latest == null ? 0 : Formats.percent(latest.getDurationMinutes(), 480), "green"));
        mini.add(new PageViews.Health.Mini("今日步数", Formats.number(steps),
                Formats.percent(steps, stepGoal), "blue"));
        mini.add(new PageViews.Health.Mini("体重",
                weight == null ? "—" : Formats.oneDecimal(weight) + " kg", 0, "yellow"));
        mini.add(new PageViews.Health.Mini("深睡占比",
                latest == null ? "—" : Formats.oneDecimal(deepRatio(latest)) + "%",
                latest == null ? 0 : (int) Math.round(deepRatio(latest)), "pink"));

        List<String> sleepLabels = new ArrayList<>();
        List<Double> deep = new ArrayList<>();
        List<Double> light = new ArrayList<>();
        List<Double> rem = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            SleepRecord record = sleepByDate.get(day);
            sleepLabels.add(Formats.weekdayShort(day));
            deep.add(record == null ? 0 : round1(record.getDeepMinutes() / 60.0));
            light.add(record == null ? 0 : round1(record.getLightMinutes() / 60.0));
            rem.add(record == null ? 0 : round1(record.getRemMinutes() / 60.0));
        }
        PageViews.Health.SleepTrend sleepTrend = new PageViews.Health.SleepTrend(sleepLabels, deep, light, rem);

        PageViews.Health.SportDuration sportDuration = sportDuration(userId, weekStart);

        List<BodyMetric> metrics = bodyMetricRepository
                .findByUserIdAndMeasuredOnBetweenOrderByMeasuredOnDesc(userId, date.minusDays(29), date);
        List<BodyMetric> ascending = new ArrayList<>(metrics);
        java.util.Collections.reverse(ascending);
        PageViews.Health.WeightCurve weightCurve = new PageViews.Health.WeightCurve(
                ascending.stream().map(m -> m.getMeasuredOn().getMonthValue() + "/" + m.getMeasuredOn().getDayOfMonth()).toList(),
                ascending.stream().map(m -> m.getWeightKg().doubleValue()).toList());

        List<PageViews.Health.SleepRow> sleepRows = sleepRepository
                .findByUserIdAndSleepDateBetweenOrderBySleepDateDesc(userId, date.minusDays(30), date)
                .stream().limit(4)
                .map(record -> new PageViews.Health.SleepRow(
                        Formats.monthDay(record.getSleepDate()),
                        Formats.clock(record.getSleepAt()),
                        Formats.durationHm(record.getDurationMinutes()),
                        Formats.oneDecimal(record.getDeepMinutes() / 60.0) + "h",
                        record.getQualityScore() == null ? "—" : record.getQualityScore() + " 分",
                        qualityLevel(record.getQualityScore())))
                .toList();

        return new PageViews.Health(hero, mini, weekGoalDays, sleepTrend, sportDuration,
                weightCurve, sleepRows, habitRows(userId, date));
    }

    private String buildDesc(SleepRecord latest, int steps, int stepGoal, int weekGoalDays) {
        if (latest == null) {
            return "还没有睡眠记录，记录一次就能看到本周趋势。";
        }
        return "本周达标 " + weekGoalDays + " 天，最近一次睡眠 " + Formats.durationHm(latest.getDurationMinutes())
                + "，今日步数 " + Formats.number(steps) + " / " + Formats.number(stepGoal) + "。";
    }

    private double deepRatio(SleepRecord record) {
        if (record.getDurationMinutes() == null || record.getDurationMinutes() == 0) {
            return 0;
        }
        return round1(record.getDeepMinutes() * 100.0 / record.getDurationMinutes());
    }

    private String qualityLevel(Integer score) {
        if (score == null) {
            return "mid";
        }
        if (score >= 85) {
            return "low";
        }
        if (score >= 65) {
            return "mid";
        }
        return "danger";
    }

    private PageViews.Health.SportDuration sportDuration(long userId, LocalDate weekStart) {
        LocalDate firstWeekStart = weekStart.minusWeeks(3);
        LocalDateTime from = firstWeekStart.atStartOfDay();
        LocalDateTime to = weekStart.plusDays(7).atStartOfDay();
        List<Workout> workouts = workoutRepository.findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, from, to);

        Map<Integer, Map<String, Integer>> byWeek = new LinkedHashMap<>();
        for (int i = 0; i < 4; i++) {
            byWeek.put(i, new HashMap<>());
        }
        for (Workout workout : workouts) {
            LocalDate day = workout.getStartedAt().toLocalDate();
            int index = (int) (Duration.between(firstWeekStart.atStartOfDay(), day.atStartOfDay()).toDays() / 7);
            if (index < 0 || index > 3) {
                continue;
            }
            byWeek.get(index).merge(workout.getWorkoutType(), workout.getDurationMinutes(), Integer::sum);
        }
        List<String> labels = new ArrayList<>();
        List<Double> run = new ArrayList<>();
        List<Double> ride = new ArrayList<>();
        List<Double> gym = new ArrayList<>();
        List<Double> other = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Map<String, Integer> week = byWeek.get(i);
            labels.add("第 " + (i + 1) + " 周");
            run.add(round1(week.getOrDefault("run", 0) / 60.0));
            ride.add(round1(week.getOrDefault("ride", 0) / 60.0));
            gym.add(round1(week.getOrDefault("gym", 0) / 60.0));
            other.add(round1(week.getOrDefault("other", 0) / 60.0));
        }
        return new PageViews.Health.SportDuration(labels, run, ride, gym, other);
    }

    private int weekGoalDays(long userId, LocalDate weekStart, LocalDate weekEnd, LocalDate date) {
        List<Habit> activeHabits = habitRepository.findByUserIdAndActiveTrueOrderBySortOrderAsc(userId);
        if (activeHabits.isEmpty()) {
            return 0;
        }
        Map<LocalDate, Set<Long>> doneByDate = habitLogRepository
                .findByUserIdAndLogDateBetween(userId, weekStart, weekEnd).stream()
                .filter(log -> Boolean.TRUE.equals(log.getDone()))
                .collect(Collectors.groupingBy(HabitLog::getLogDate,
                        Collectors.mapping(HabitLog::getHabitId, Collectors.toSet())));
        LocalDate lastDay = date.isBefore(weekEnd) ? date : weekEnd;
        int goalDays = 0;
        for (LocalDate day = weekStart; !day.isAfter(lastDay); day = day.plusDays(1)) {
            Set<Long> done = doneByDate.get(day);
            if (done != null && done.size() >= activeHabits.size()) {
                goalDays++;
            }
        }
        return goalDays;
    }

    private List<PageViews.Health.HabitRow> habitRows(long userId, LocalDate date) {
        LocalDate weekStart = weekStart(date);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<HabitLog> weekLogs = habitLogRepository.findByUserIdAndLogDateBetween(userId, weekStart, weekEnd);
        Map<Long, HabitLog> todayLogs = weekLogs.stream()
                .filter(log -> log.getLogDate().equals(date))
                .collect(Collectors.toMap(HabitLog::getHabitId, log -> log, (a, b) -> a));

        List<PageViews.Health.HabitRow> rows = new ArrayList<>();
        for (Habit habit : habitRepository.findByUserIdAndActiveTrueOrderBySortOrderAsc(userId)) {
            HabitLog log = todayLogs.get(habit.getId());
            boolean weekPeriod = "week".equals(habit.getPeriod());
            double value = weekPeriod
                    ? weekLogs.stream().filter(l -> l.getHabitId().equals(habit.getId()) && Boolean.TRUE.equals(l.getDone())).count()
                    : log == null ? 0 : log.getValue().doubleValue();
            double target = habit.getTargetValue().doubleValue();
            boolean done = weekPeriod
                    ? value >= target
                    : log != null && (Boolean.TRUE.equals(log.getDone()) || value >= target);
            String valueText = weekPeriod
                    ? Math.round(value) + " / " + Formats.plain(habit.getTargetValue()) + " " + habit.getUnit()
                    : Formats.plain(BigDecimal.valueOf(value)) + " / " + Formats.plain(habit.getTargetValue()) + " " + habit.getUnit();
            rows.add(new PageViews.Health.HabitRow(
                    String.valueOf(habit.getId()), habit.getIcon(), habit.getName(), valueText,
                    Formats.percent(value, target), habit.getColor(), done));
        }
        return rows;
    }

    // ---------- 写入 ----------

    @Transactional
    public HealthDtos.SleepRecordDto saveSleep(HealthDtos.SleepRecordRequest request) {
        if (!request.wakeAt().isAfter(request.sleepAt())) {
            throw ApiException.badRequest("起床时间必须晚于入睡时间");
        }
        long userId = CurrentUser.ID;
        LocalDate sleepDate = request.wakeAt().toLocalDate();
        SleepRecord record = sleepRepository
                .findByUserIdAndSleepDateBetweenOrderBySleepDateDesc(userId, sleepDate, sleepDate)
                .stream().findFirst().orElseGet(SleepRecord::new);
        record.setUserId(userId);
        record.setSleepDate(sleepDate);
        record.setSleepAt(request.sleepAt());
        record.setWakeAt(request.wakeAt());
        int duration = (int) Duration.between(request.sleepAt(), request.wakeAt()).toMinutes();
        record.setDurationMinutes(duration);
        record.setDeepMinutes(nz(request.deepMinutes()));
        record.setLightMinutes(nz(request.lightMinutes()));
        record.setRemMinutes(nz(request.remMinutes()));
        record.setAwakeMinutes(nz(request.awakeMinutes()));
        record.setQualityScore(request.qualityScore());
        record.setNote(request.note());
        record.setSource("manual");
        SleepRecord saved = sleepRepository.save(record);
        return new HealthDtos.SleepRecordDto(String.valueOf(saved.getId()), saved.getSleepDate(),
                Formats.offset(saved.getSleepAt()).toString(), Formats.offset(saved.getWakeAt()).toString(),
                saved.getDurationMinutes(), saved.getDeepMinutes(), saved.getLightMinutes(),
                saved.getRemMinutes(), saved.getAwakeMinutes(), saved.getQualityScore(), saved.getNote());
    }

    @Transactional
    public HealthDtos.DailyActivityDto saveActivity(LocalDate day, HealthDtos.DailyActivityRequest request) {
        long userId = CurrentUser.ID;
        DailyActivity activity = activityRepository.findByUserIdAndActivityDate(userId, day)
                .orElseGet(DailyActivity::new);
        activity.setUserId(userId);
        activity.setActivityDate(day);
        activity.setSteps(request.steps());
        activity.setStepGoal(request.stepGoal() == null ? 10000 : request.stepGoal());
        activity.setDistanceKm(request.distanceKm());
        activity.setActiveCaloriesKcal(request.activeCaloriesKcal());
        activity.setSource("manual");
        DailyActivity saved = activityRepository.save(activity);
        return new HealthDtos.DailyActivityDto(saved.getActivityDate(), saved.getSteps(), saved.getStepGoal(),
                saved.getDistanceKm(), saved.getActiveCaloriesKcal());
    }

    @Transactional
    public HealthDtos.WorkoutDto createWorkout(HealthDtos.WorkoutRequest request) {
        Workout workout = new Workout();
        workout.setUserId(CurrentUser.ID);
        workout.setWorkoutType(request.workoutType());
        workout.setStartedAt(request.startedAt());
        workout.setDurationMinutes(request.durationMinutes());
        workout.setDistanceKm(request.distanceKm());
        workout.setCaloriesKcal(request.caloriesKcal());
        workout.setNote(request.note());
        workout.setSource("manual");
        Workout saved = workoutRepository.save(workout);
        return toWorkoutDto(saved);
    }

    public List<HealthDtos.WorkoutDto> workouts(LocalDate from, LocalDate to) {
        return workoutRepository
                .findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(CurrentUser.ID,
                        from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                .stream().map(this::toWorkoutDto).toList();
    }

    private HealthDtos.WorkoutDto toWorkoutDto(Workout workout) {
        return new HealthDtos.WorkoutDto(String.valueOf(workout.getId()), workout.getWorkoutType(),
                Formats.offset(workout.getStartedAt()).toString(), workout.getDurationMinutes(),
                workout.getDistanceKm(), workout.getCaloriesKcal(), workout.getNote());
    }

    @Transactional
    public HealthDtos.BodyMetricDto saveBodyMetric(HealthDtos.BodyMetricRequest request) {
        long userId = CurrentUser.ID;
        List<BodyMetric> existing = bodyMetricRepository
                .findByUserIdAndMeasuredOnBetweenOrderByMeasuredOnDesc(userId, request.measuredOn(), request.measuredOn());
        BodyMetric metric = existing.isEmpty() ? new BodyMetric() : existing.get(0);
        metric.setUserId(userId);
        metric.setMeasuredOn(request.measuredOn());
        metric.setWeightKg(request.weightKg());
        metric.setBodyFatPercent(request.bodyFatPercent());
        metric.setMuscleKg(request.muscleKg());
        metric.setSource("manual");
        BodyMetric saved = bodyMetricRepository.save(metric);
        return new HealthDtos.BodyMetricDto(String.valueOf(saved.getId()), saved.getMeasuredOn(),
                saved.getWeightKg(), saved.getBodyFatPercent(), saved.getMuscleKg());
    }

    @Transactional
    public HealthDtos.HabitCheckInResult checkIn(Long habitId, LocalDate day, HealthDtos.HabitCheckInRequest request) {
        long userId = CurrentUser.ID;
        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> ApiException.notFound("习惯不存在"));
        HabitLog log = habitLogRepository.findByHabitIdAndLogDate(habitId, day).orElseGet(HabitLog::new);
        log.setUserId(userId);
        log.setHabitId(habitId);
        log.setLogDate(day);
        BigDecimal value = request.value() == null ? (log.getValue() == null ? BigDecimal.ZERO : log.getValue()) : request.value();
        log.setValue(value);
        boolean done = request.done() != null
                ? request.done()
                : value.compareTo(habit.getTargetValue()) >= 0;
        log.setDone(done);
        habitLogRepository.save(log);
        return new HealthDtos.HabitCheckInResult(String.valueOf(habitId), day, done,
                Formats.plain(value), Formats.plain(habit.getTargetValue()), habit.getUnit(),
                Formats.percent(value.doubleValue(), habit.getTargetValue().doubleValue()));
    }

    // ---------- 工具 ----------

    private LocalDate weekStart(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1L);
    }

    private double round1(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private int nz(Integer value) {
        return value == null ? 0 : value;
    }
}
