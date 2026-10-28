package dev.xichen.backend.controller;

import dev.xichen.backend.dto.TaskDtos;
import dev.xichen.backend.dto.WorkDtos;
import dev.xichen.backend.service.WorkService;
import dev.xichen.backend.view.PageViews;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/work")
public class WorkController {
    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping("/overview")
    public PageViews.Work overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String month) {
        return workService.overview(date, month == null ? null : YearMonth.parse(month));
    }

    @GetMapping("/tasks")
    public TaskDtos.TaskPage tasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Boolean done,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return workService.tasks(date, area, done, page, size);
    }

    @PostMapping("/tasks")
    public TaskDtos.TaskDto createTask(@Valid @RequestBody TaskDtos.CreateRequest request) {
        return workService.createTask(request);
    }

    @PatchMapping("/tasks/{id}")
    public TaskDtos.TaskDto patchTask(@PathVariable("id") Long id,
                                      @Valid @RequestBody TaskDtos.PatchRequest request) {
        return workService.patchTask(id, request);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        workService.deleteTask(id);
    }

    @PostMapping("/projects")
    public WorkDtos.ProjectDto createProject(@Valid @RequestBody WorkDtos.ProjectRequest request) {
        return workService.createProject(request);
    }

    @PutMapping("/projects/{id}")
    public WorkDtos.ProjectDto updateProject(@PathVariable("id") Long id,
                                             @Valid @RequestBody WorkDtos.ProjectRequest request) {
        return workService.updateProject(id, request);
    }
}
