package dev.xichen.backend.controller;

import dev.xichen.backend.dto.LifeDtos;
import dev.xichen.backend.service.LifeService;
import dev.xichen.backend.view.PageViews;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/life")
public class LifeController {
    private final LifeService lifeService;

    public LifeController(LifeService lifeService) {
        this.lifeService = lifeService;
    }

    @GetMapping("/overview")
    public PageViews.Life overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String month) {
        return lifeService.overview(date, month == null ? null : YearMonth.parse(month));
    }

    @GetMapping("/activities")
    public List<LifeDtos.ActivityDto> activities(@RequestParam(required = false) Integer year,
                                                 @RequestParam(required = false) Integer month) {
        return lifeService.activities(year, month);
    }

    @PostMapping("/activities")
    public LifeDtos.ActivityDto createActivity(@Valid @RequestBody LifeDtos.ActivityRequest request) {
        return lifeService.createActivity(request);
    }

    @DeleteMapping("/activities/{id}")
    public void deleteActivity(@PathVariable("id") Long id) {
        lifeService.deleteActivity(id);
    }

    @GetMapping("/media")
    public List<LifeDtos.MediaDto> media(@RequestParam(required = false) String type,
                                        @RequestParam(required = false) String status) {
        return lifeService.media(type, status);
    }

    @PostMapping("/media")
    public LifeDtos.MediaDto createMedia(@Valid @RequestBody LifeDtos.MediaRequest request) {
        return lifeService.createMedia(request);
    }

    @PatchMapping("/media/{id}")
    public LifeDtos.MediaDto patchMedia(@PathVariable("id") Long id,
                                        @Valid @RequestBody LifeDtos.MediaPatch patch) {
        return lifeService.patchMedia(id, patch);
    }

    @DeleteMapping("/media/{id}")
    public void deleteMedia(@PathVariable("id") Long id) {
        lifeService.deleteMedia(id);
    }
}
