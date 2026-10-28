package dev.xichen.backend.controller;

import dev.xichen.backend.dto.HealthDtos;
import dev.xichen.backend.service.HealthService;
import dev.xichen.backend.view.PageViews;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/overview")
    public PageViews.Health overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return healthService.overview(date);
    }

    @PostMapping("/sleep-records")
    public HealthDtos.SleepRecordDto createSleep(@Valid @RequestBody HealthDtos.SleepRecordRequest request) {
        return healthService.saveSleep(request);
    }

    @PutMapping("/daily-activities/{date}")
    public HealthDtos.DailyActivityDto saveActivity(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody HealthDtos.DailyActivityRequest request) {
        return healthService.saveActivity(date, request);
    }

    @PostMapping("/workouts")
    public HealthDtos.WorkoutDto createWorkout(@Valid @RequestBody HealthDtos.WorkoutRequest request) {
        return healthService.createWorkout(request);
    }

    @GetMapping("/workouts")
    public List<HealthDtos.WorkoutDto> workouts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return healthService.workouts(from, to);
    }

    @PostMapping("/body-metrics")
    public HealthDtos.BodyMetricDto saveBodyMetric(@Valid @RequestBody HealthDtos.BodyMetricRequest request) {
        return healthService.saveBodyMetric(request);
    }

    @PutMapping("/habits/{habitId}/check-ins/{date}")
    public HealthDtos.HabitCheckInResult checkIn(
            @PathVariable Long habitId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody HealthDtos.HabitCheckInRequest request) {
        return healthService.checkIn(habitId, date, request);
    }
}
