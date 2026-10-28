package dev.xichen.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一的页面视图结构。字段名与前端 mock 保持一致，前端可直接渲染。
 */
public final class Views {
    private Views() {
    }

    public record Banner(String title, String desc, java.util.List<String> chips) {
    }

    public record Metric(String key, String label, String value, String trend, String trendText, String foot) {
    }

    public record Goal(String name, double percent, String valueText, String subText, String color) {
    }

    public record ProgressItem(String name, String valueText, int percent, String color) {
    }

    public record SeriesPoint(String label, double value) {
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record LineChart(java.util.List<String> labels, java.util.List<Double> data) {
    }
}
