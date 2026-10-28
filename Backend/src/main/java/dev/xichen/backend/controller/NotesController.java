package dev.xichen.backend.controller;

import dev.xichen.backend.dto.NoteDtos;
import dev.xichen.backend.service.NotesIndexService;
import dev.xichen.backend.view.PageViews;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {
    private final NotesIndexService notesIndexService;

    public NotesController(NotesIndexService notesIndexService) {
        this.notesIndexService = notesIndexService;
    }

    @GetMapping("/overview")
    public PageViews.Notes overview() {
        return notesIndexService.overview();
    }

    @GetMapping("/tree")
    public List<PageViews.Notes.FolderRow> tree() {
        return notesIndexService.tree();
    }

    @GetMapping("/tags")
    public List<PageViews.Notes.TagStat> tags(@RequestParam(defaultValue = "8") int limit) {
        return notesIndexService.tagStats(limit);
    }

    @GetMapping
    public NotesIndexService.NotesPage list(@RequestParam(required = false) String folder,
                                            @RequestParam(name = "q", required = false) String query,
                                            @RequestParam(required = false) String tag,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return notesIndexService.list(folder, query, tag, page, size);
    }

    @GetMapping("/content")
    public PageViews.NoteDetail content(@RequestParam String path) {
        return notesIndexService.content(path);
    }

    @PostMapping("/rescan")
    public NoteDtos.RescanResult rescan() {
        return notesIndexService.rescanResult();
    }
}
