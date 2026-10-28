package dev.xichen.backend.service;

import dev.xichen.backend.common.ApiException;
import dev.xichen.backend.common.CurrentUser;
import dev.xichen.backend.common.Formats;
import dev.xichen.backend.common.Styles;
import dev.xichen.backend.dto.NoteDtos;
import dev.xichen.backend.entity.DataSource;
import dev.xichen.backend.repository.DataSourceRepository;
import dev.xichen.backend.view.PageViews;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 笔记以 Markdown 文件夹为唯一事实来源：启动时扫描，30 秒内复用索引，支持手动重建。
 */
@Service
public class NotesIndexService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final Pattern TAG_PATTERN = Pattern.compile("#([\\p{IsHan}A-Za-z0-9_-]+)");
    private static final Pattern TITLE_PATTERN = Pattern.compile("(?m)^title:\\s*(.+)$");
    private static final Pattern CREATED_PATTERN = Pattern.compile("(?m)^created:\\s*(.+)$");
    private static final Pattern TAGS_PATTERN = Pattern.compile("(?m)^tags:\\s*(.*)$");
    private static final Pattern PRIMARY_PATTERN = Pattern.compile("(?m)^primaryTag:\\s*(.+)$");
    private static final Pattern VAULT_PATTERN = Pattern.compile("\"vaultPath\"\\s*:\\s*\"([^\"]*)\"");
    private static final long RESCAN_INTERVAL_SECONDS = 30;

    private final DataSourceRepository dataSourceRepository;

    private volatile List<Entry> entries = List.of();
    private volatile Instant lastScan = Instant.EPOCH;

    public NotesIndexService(DataSourceRepository dataSourceRepository) {
        this.dataSourceRepository = dataSourceRepository;
    }

    private record Entry(
            String path,
            String folder,
            String title,
            List<String> tags,
            String primaryTag,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int words) {
    }

    // ---------- 索引 ----------

    public synchronized int rescan() {
        Path root = vaultRoot();
        if (root == null) {
            entries = List.of();
            lastScan = Instant.now();
            return 0;
        }
        List<Entry> scanned = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root, 8)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".md"))
                    .filter(path -> !isHidden(root.relativize(path)))
                    .forEach(path -> {
                        Entry entry = parse(path, root);
                        if (entry != null) {
                            scanned.add(entry);
                        }
                    });
        } catch (IOException ex) {
            throw new ApiException(503, "笔记目录不可访问：" + ex.getMessage());
        }
        scanned.sort(Comparator.comparing(Entry::updatedAt).reversed());
        entries = List.copyOf(scanned);
        lastScan = Instant.now();
        return entries.size();
    }

    private void ensureFresh() {
        if (Instant.now().getEpochSecond() - lastScan.getEpochSecond() > RESCAN_INTERVAL_SECONDS) {
            rescan();
        }
    }

    private List<Entry> current() {
        ensureFresh();
        return entries;
    }

    private Path vaultRoot() {
        String configured = null;
        for (DataSource source : dataSourceRepository.findByUserIdOrderByIdAsc(CurrentUser.ID)) {
            if ("obsidian".equals(source.getSourceType()) && source.getConfigJson() != null) {
                Matcher matcher = VAULT_PATTERN.matcher(source.getConfigJson());
                if (matcher.find()) {
                    configured = matcher.group(1);
                }
            }
        }
        String value = (configured == null || configured.isBlank()) ? "notes-vault" : configured.trim();
        Path path = Paths.get(value);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path);
        }
        path = path.normalize().toAbsolutePath();
        return Files.isDirectory(path) ? path : null;
    }

    public String vaultPathLabel() {
        Path root = vaultRoot();
        return root == null ? null : root.toString();
    }

    private boolean isHidden(Path relative) {
        for (Path part : relative) {
            String name = part.toString();
            if (name.startsWith(".") || name.equals("node_modules")) {
                return true;
            }
        }
        return false;
    }

    private Entry parse(Path file, Path root) {
        try {
            String raw = Files.readString(file, StandardCharsets.UTF_8);
            String[] split = splitFrontMatter(raw);
            String front = split[0];
            String body = split[1];

            Path relative = root.relativize(file);
            String path = relative.toString().replace('\\', '/');
            String folder = relative.getParent() == null
                    ? null
                    : relative.getParent().toString().replace('\\', '/');

            String title = match(TITLE_PATTERN, front);
            if (title == null) {
                title = firstHeading(body);
            }
            if (title == null) {
                String fileName = file.getFileName().toString();
                title = fileName.substring(0, fileName.length() - 3);
            }

            List<String> tags = parseTags(front, body);
            String primary = match(PRIMARY_PATTERN, front);
            if (primary == null || primary.isBlank()) {
                primary = tags.isEmpty() ? null : tags.get(0);
            }

            BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
            LocalDateTime modified = LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZONE);
            String createdText = match(CREATED_PATTERN, front);
            LocalDateTime created = parseDate(createdText);
            if (created == null) {
                created = LocalDateTime.ofInstant(attributes.creationTime().toInstant(), ZONE);
            }
            return new Entry(path, folder, title, tags, primary, created, modified, countWords(body));
        } catch (IOException ex) {
            return null;
        }
    }

    private static String[] splitFrontMatter(String raw) {
        String normalized = raw.replace("\r\n", "\n");
        if (!normalized.startsWith("---")) {
            return new String[]{"", normalized};
        }
        int end = normalized.indexOf("\n---", 3);
        if (end < 0) {
            return new String[]{"", normalized};
        }
        String front = normalized.substring(3, end);
        String body = normalized.substring(end + 4);
        return new String[]{front, body};
    }

    private static String match(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private static String firstHeading(String body) {
        for (String line : body.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("# ")) {
                return trimmed.substring(2).trim();
            }
        }
        return null;
    }

    private static List<String> parseTags(String front, String body) {
        List<String> tags = new ArrayList<>();
        Matcher matcher = TAGS_PATTERN.matcher(front);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            if (value.startsWith("[")) {
                value = value.substring(1, Math.max(value.length() - 1, 1));
            }
            for (String item : value.split(",")) {
                addTag(tags, item);
            }
            if (value.isBlank()) {
                String[] lines = front.substring(matcher.end()).split("\n");
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("- ")) {
                        addTag(tags, trimmed.substring(1));
                    } else if (!trimmed.isEmpty()) {
                        break;
                    }
                }
            }
        }
        for (String line : stripCodeFences(body).split("\n")) {
            Matcher inline = TAG_PATTERN.matcher(line);
            while (inline.find()) {
                addTag(tags, inline.group(1));
            }
        }
        return tags;
    }

    private static void addTag(List<String> tags, String raw) {
        String value = raw.trim().replace("\"", "").replace("'", "").replace("#", "").trim();
        if (!value.isEmpty() && !tags.contains(value)) {
            tags.add(value);
        }
    }

    private static String stripCodeFences(String text) {
        return text.replaceAll("(?s)```.*?```", " ");
    }

    private static int countWords(String body) {
        String plain = stripCodeFences(body)
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("[#>*`\\-\\[\\]()!|_]", "")
                .replaceAll("\\s", "");
        return plain.length();
    }

    private static LocalDateTime parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String cleaned = value.trim().replace("\"", "");
        try {
            if (cleaned.length() == 10) {
                return LocalDate.parse(cleaned).atStartOfDay();
            }
            return LocalDateTime.parse(cleaned);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    // ---------- 页面聚合 ----------

    public PageViews.Notes overview() {
        List<Entry> all = current();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);

        int addedThisMonth = (int) all.stream().filter(e -> !e.createdAt().toLocalDate().isBefore(monthStart)).count();
        int editedThisWeek = (int) all.stream().filter(e -> !e.updatedAt().toLocalDate().isBefore(weekStart)).count();
        int tagCount = (int) all.stream().flatMap(e -> e.tags().stream()).distinct().count();

        List<PageViews.Notes.FolderRow> folders = new ArrayList<>();
        for (String folder : folderIds(all)) {
            long count = all.stream()
                    .filter(e -> e.folder() != null && (e.folder().equals(folder) || e.folder().startsWith(folder + "/")))
                    .count();
            String name = folder.contains("/") ? folder.substring(folder.lastIndexOf('/') + 1) : folder;
            String parent = folder.contains("/") ? folder.substring(0, folder.lastIndexOf('/')) : null;
            folders.add(new PageViews.Notes.FolderRow(folder, Styles.folderIcon(name), name, (int) count, parent));
        }

        List<PageViews.Notes.TagStat> stats = tagStats(8);
        List<PageViews.Notes.NoteView> recent = all.stream()
                .limit(5)
                .map(this::toNoteView)
                .toList();

        String activeFolderId = all.isEmpty() ? null : all.get(0).folder();
        PageViews.Notes.NoteView activeNote = all.isEmpty() ? null : toNoteView(all.get(0));

        String title = addedThisMonth > 0 ? "本月写作状态不错" : "笔记库已就绪";
        String desc = "共 " + all.size() + " 篇笔记，本周编辑 " + editedThisWeek + " 篇，标签覆盖 " + tagCount + " 个。";

        return new PageViews.Notes(
                new PageViews.Notes.Banner(title, desc,
                        List.of("总量 " + all.size() + " 篇", "本月 +" + addedThisMonth, "本周 " + editedThisWeek + " 篇"),
                        all.size(), addedThisMonth, editedThisWeek),
                List.of(
                        new PageViews.Notes.MetricSlim("笔记总量", String.valueOf(all.size()), "篇"),
                        new PageViews.Notes.MetricSlim("本月新增", String.valueOf(addedThisMonth), "篇"),
                        new PageViews.Notes.MetricSlim("标签数", String.valueOf(tagCount), "个"),
                        new PageViews.Notes.MetricSlim("本周编辑", String.valueOf(editedThisWeek), "篇")),
                folders, recent, stats, activeFolderId, activeNote);
    }

    public List<PageViews.Notes.FolderRow> tree() {
        return overview().folders();
    }

    public List<PageViews.Notes.TagStat> tagStats(int limit) {
        List<Entry> all = current();
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Entry entry : all) {
            for (String tag : entry.tags()) {
                counts.merge(tag, 1, Integer::sum);
            }
        }
        int max = counts.values().stream().max(Integer::compareTo).orElse(0);
        List<Map.Entry<String, Integer>> sorted = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .toList();
        List<PageViews.Notes.TagStat> result = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, Integer> item = sorted.get(i);
            int percent = max == 0 ? 0 : Math.round(item.getValue() * 100f / max);
            result.add(new PageViews.Notes.TagStat(item.getKey(), item.getValue(), percent, Styles.rotateBar(i)));
        }
        return result;
    }

    public record NotesPage(List<PageViews.Notes.NoteView> items, int total, int page, int size) {
    }

    public NotesPage list(String folder, String q, String tag, int page, int size) {
        String keyword = q == null ? null : q.trim().toLowerCase();
        List<PageViews.Notes.NoteView> filtered = current().stream()
                .filter(e -> folder == null || folder.isBlank()
                        || (e.folder() != null && (e.folder().equals(folder) || e.folder().startsWith(folder + "/"))))
                .filter(e -> keyword == null || keyword.isEmpty() || e.title().toLowerCase().contains(keyword))
                .filter(e -> tag == null || tag.isBlank() || e.tags().contains(tag.replace("#", "")))
                .map(this::toNoteView)
                .toList();
        int from = Math.max(0, (page - 1) * size);
        int to = Math.min(filtered.size(), from + size);
        List<PageViews.Notes.NoteView> items = from >= filtered.size() ? List.of() : filtered.subList(from, to);
        return new NotesPage(items, filtered.size(), page, size);
    }

    public PageViews.NoteDetail content(String path) {
        Path root = vaultRoot();
        if (root == null) {
            throw new ApiException(503, "笔记目录不可访问");
        }
        Path file = root.resolve(path).normalize();
        if (!file.startsWith(root)) {
            throw new ApiException(403, "非法路径");
        }
        if (!Files.isRegularFile(file) || !file.getFileName().toString().toLowerCase().endsWith(".md")) {
            throw ApiException.notFound("笔记不存在：" + path);
        }
        Entry entry = parse(file, root);
        if (entry == null) {
            throw ApiException.notFound("笔记不存在：" + path);
        }
        String body;
        try {
            body = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new ApiException(503, "笔记读取失败");
        }
        String markdown = splitFrontMatter(body)[1].trim();
        List<PageViews.Notes.Block> blocks = toBlocks(markdown);
        List<PageViews.Notes.TagRef> tags = entry.tags().stream()
                .map(name -> new PageViews.Notes.TagRef("#" + name, Styles.tagPill(name)))
                .toList();
        PageViews.Notes.TagRef primary = entry.primaryTag() == null
                ? null
                : new PageViews.Notes.TagRef("#" + entry.primaryTag(), Styles.tagPill(entry.primaryTag()));
        return new PageViews.NoteDetail(entry.path(), entry.title(), toHtml(blocks), markdown,
                entry.words(), blocks, primary, tags,
                Formats.offset(entry.createdAt()).toString(), Formats.offset(entry.updatedAt()).toString());
    }

    public NoteDtos.RescanResult rescanResult() {
        int indexed = rescan();
        return new NoteDtos.RescanResult(indexed, vaultPathLabel());
    }

    // ---------- 映射 ----------

    private PageViews.Notes.NoteView toNoteView(Entry entry) {
        String body;
        try {
            body = Files.readString(resolveEntry(entry.path()), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            body = "";
        }
        List<PageViews.Notes.Block> blocks = toBlocks(splitFrontMatter(body)[1]);
        List<PageViews.Notes.TagRef> tagRefs = entry.tags().stream()
                .map(name -> new PageViews.Notes.TagRef("#" + name, Styles.tagPill(name)))
                .toList();
        String primary = entry.primaryTag();
        return new PageViews.Notes.NoteView(
                entry.path(),
                entry.path(),
                entry.folder(),
                entry.title(),
                primary == null ? "" : "#" + primary,
                Styles.tagPill(primary),
                Formats.timeAgo(entry.updatedAt()),
                entry.words(),
                blocks,
                tagRefs,
                Formats.offset(entry.updatedAt()).toString());
    }

    private Path resolveEntry(String path) {
        Path root = vaultRoot();
        return root == null ? Paths.get(path) : root.resolve(path);
    }

    private List<String> folderIds(List<Entry> all) {
        TreeSet<String> folders = new TreeSet<>();
        for (Entry entry : all) {
            String folder = entry.folder();
            while (folder != null && !folder.isEmpty()) {
                folders.add(folder);
                folder = folder.contains("/") ? folder.substring(0, folder.lastIndexOf('/')) : null;
            }
        }
        return new ArrayList<>(folders);
    }

    private static List<PageViews.Notes.Block> toBlocks(String markdown) {
        List<PageViews.Notes.Block> blocks = new ArrayList<>();
        StringBuilder paragraph = new StringBuilder();
        boolean inCode = false;
        for (String line : markdown.replace("\r\n", "\n").split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("```")) {
                inCode = !inCode;
                continue;
            }
            if (trimmed.isEmpty()) {
                flush(blocks, paragraph);
                continue;
            }
            if (!inCode && trimmed.startsWith("#")) {
                flush(blocks, paragraph);
                String text = trimmed.replaceAll("^#+\\s*", "");
                if (!text.isEmpty()) {
                    blocks.add(new PageViews.Notes.Block(trimmed.startsWith("# ") ? "h1" : "h4", text));
                }
                continue;
            }
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                flush(blocks, paragraph);
                blocks.add(new PageViews.Notes.Block("p", "· " + trimmed.substring(2)));
                continue;
            }
            if (trimmed.startsWith("> ")) {
                flush(blocks, paragraph);
                blocks.add(new PageViews.Notes.Block("p", trimmed.substring(2)));
                continue;
            }
            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(trimmed);
        }
        flush(blocks, paragraph);
        blocks.removeIf(block -> "h1".equals(block.type()));
        return blocks;
    }

    private static void flush(List<PageViews.Notes.Block> blocks, StringBuilder paragraph) {
        if (paragraph.length() > 0) {
            blocks.add(new PageViews.Notes.Block("p", paragraph.toString().replace("**", "")));
            paragraph.setLength(0);
        }
    }

    private static String toHtml(List<PageViews.Notes.Block> blocks) {
        StringBuilder html = new StringBuilder();
        for (PageViews.Notes.Block block : blocks) {
            String text = escape(block.text());
            if ("h4".equals(block.type())) {
                html.append("<h4>").append(text).append("</h4>");
            } else {
                html.append("<p>").append(text).append("</p>");
            }
        }
        return html.toString();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
