package dev.xichen.backend.controller;

import dev.xichen.backend.dto.MeDtos;
import dev.xichen.backend.service.MeService;
import dev.xichen.backend.view.PageViews;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping
    public PageViews.Me profile() {
        return meService.profile();
    }

    @PatchMapping
    public PageViews.Me.Profile update(@Valid @RequestBody MeDtos.MePatch patch) {
        return meService.updateProfile(patch);
    }

    @GetMapping("/integrations")
    public List<PageViews.Me.DataSourceRow> integrations() {
        return meService.integrations();
    }

    @PatchMapping("/integrations/{sourceType}")
    public PageViews.Me.DataSourceRow updateIntegration(@PathVariable("sourceType") String sourceType,
                                                        @RequestBody MeDtos.IntegrationPatch patch) {
        return meService.updateIntegration(sourceType, patch);
    }
}
