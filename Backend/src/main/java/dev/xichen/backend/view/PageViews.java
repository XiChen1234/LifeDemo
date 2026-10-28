package dev.xichen.backend.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.xichen.backend.common.Views;

import java.util.List;

/**
 * 聚合页面视图：字段与前端 store 一一对应，页面可直接渲染。
 */
public final class PageViews {
    private PageViews() {
    }

    public record Dashboard(
            String date,
            Hero hero,
            List<Views.Metric> metrics,
            List<Views.Goal> yearGoals,
            CashTrend cashTrend,
            List<TaskItem> todayTasks,
            List<Views.ProgressItem> expenseStructure,
            List<Views.ProgressItem> mediaProgress,
            List<RecentNote> recentNotes) {

        public record Hero(
                String displayName,
                int streakDays,
                int weekCompletionRate,
                int targetYear,
                String greeting,
                String quote,
                List<String> chips,
                String dateMain,
                String dateSub) {
        }

        public record CashTrend(List<String> labels, List<Integer> income, List<Integer> expense) {
        }

        public record TaskItem(
                String id,
                String text,
                String title,
                String area,
                String priority,
                boolean done,
                String scheduledDate,
                String dueAt) {
        }

        public record RecentNote(
                String path,
                String id,
                String title,
                String tag,
                String timeAgo,
                String updatedAt) {
        }
    }

    public record Health(
            Hero hero,
            List<Mini> mini,
            int weekGoalDays,
            SleepTrend sleepTrend,
            SportDuration sportDuration,
            WeightCurve weightCurve,
            List<SleepRow> sleepRecords,
            List<HabitRow> habits) {

        public record Hero(
                String title,
                String desc,
                List<String> chips,
                Integer sleepMinutes,
                Double deepRatio,
                Integer steps,
                Double weightKg) {
        }

        public record Mini(String label, String value, int percent, String color) {
        }

        public record SleepTrend(List<String> labels, List<Double> deep, List<Double> light, List<Double> rem) {
        }

        public record SportDuration(List<String> labels, List<Double> run, List<Double> ride,
                                    List<Double> gym, List<Double> other) {
        }

        public record WeightCurve(List<String> labels, List<Double> data) {
        }

        public record SleepRow(String date, String sleepTime, String duration, String deep,
                               String quality, String qualityLevel) {
        }

        public record HabitRow(String id, String icon, String name, String valueText,
                               int percent, String color, boolean done) {
        }
    }

    public record Wealth(
            Banner banner,
            List<Views.Metric> metrics,
            MonthCash monthCash,
            Chart expense6m,
            List<CategorySlice> expenseCategory,
            List<Views.Goal> savingGoals,
            List<TxRow> transactions,
            List<BudgetRow> budgets) {

        public record Banner(String title, String desc, List<String> chips,
                             int totalAssets, int balance, double savingsRate) {
        }

        public record MonthCash(List<String> labels, List<Integer> income, List<Integer> expense) {
        }

        public record Chart(List<String> labels, List<Integer> data) {
        }

        public record CategorySlice(String label, int value) {
        }

        public record TxRow(String id, String category, String categoryClass, String title,
                            String time, String amount, boolean isIncome) {
        }

        public record BudgetRow(String name, long used, long total, int percent, String color) {
        }
    }

    public record Work(
            Banner banner,
            CalendarMeta calendar,
            List<CalendarEventRow> calendarEvents,
            List<Dashboard.TaskItem> todos,
            List<DeadlineRow> deadlines,
            List<ProjectRow> projects) {

        public record Banner(String title, String desc, List<String> chips,
                             int highPriorityOpen, int weekDeadlines) {
        }

        public record CalendarMeta(int year, int month, int today, String title, String sub) {
        }

        public record CalendarEventRow(int day, String text, String level, Boolean isDdl) {
        }

        public record DeadlineRow(String title, String time, String priority) {
        }

        public record ProjectRow(String id, String name, double percent, String stage, String color) {
        }
    }

    public record Life(
            Banner banner,
            List<Views.Metric> metrics,
            List<Slice> activityDist,
            List<Slice> mediaDist,
            List<Views.ProgressItem> reading,
            List<Views.ProgressItem> watching,
            List<MediaRow> finishedMedia) {

        public record Banner(String title, String desc, List<String> chips,
                             int activityCount, int booksFinished, int mediaFinished) {
        }

        public record Slice(String label, int value) {
        }

        public record MediaRow(String id, String type, String typeClass, String title, String meta, int rating) {
        }
    }

    public record Notes(
            Banner banner,
            List<MetricSlim> metrics,
            List<FolderRow> folders,
            List<NoteView> recentNotes,
            List<TagStat> tagStats,
            String activeFolderId,
            NoteView activeNote) {

        public record Banner(String title, String desc, List<String> chips,
                             int total, int addedThisMonth, int editedThisWeek) {
        }

        public record MetricSlim(String label, String value, String unit) {
        }

        public record FolderRow(String id, String icon, String name, int count, String parentId) {
        }

        public record NoteView(
                String id,
                String path,
                String folderId,
                String title,
                String tag,
                String tagClass,
                String timeAgo,
                int words,
                List<Block> paragraphs,
                List<TagRef> tags,
                String updatedAt) {
        }

        public record Block(String type, String text) {
        }

        public record TagRef(String text, @JsonProperty("class") String className) {
        }

        public record TagStat(String name, int count, int percent, String color) {
        }
    }

    public record NoteDetail(String path, String title, String contentHtml, String contentMarkdown,
                             int wordCount, List<Notes.Block> paragraphs, Notes.TagRef primaryTag,
                             List<Notes.TagRef> tags, String createdAt, String updatedAt) {
    }

    public record Me(Profile user, Meta meta, List<DataSourceRow> dataSources) {

        public record Profile(String name, String avatarChar, int usedDays, int targetYear, String signature) {
        }

        public record Meta(String version, String theme, String lastUpdate, String dataUpdatedAt, String techStack) {
        }

        public record DataSourceRow(String id, String icon, String iconBg, String iconColor,
                                    String name, String desc, String status) {
        }
    }
}
