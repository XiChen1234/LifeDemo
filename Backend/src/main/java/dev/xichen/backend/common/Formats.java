package dev.xichen.backend.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 展示层格式化：前端页面直接消费字符串，避免在多个接口里重复拼接。
 */
public final class Formats {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter HOUR_MINUTE = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] MONTH_NAMES = {
            "一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"
    };

    private Formats() {
    }

    public static OffsetDateTime offset(LocalDateTime value) {
        return value == null ? null : value.atZone(ZONE).toOffsetDateTime();
    }

    public static String money(BigDecimal value) {
        if (value == null) {
            return "¥0";
        }
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        boolean whole = scaled.scale() <= 0;
        DecimalFormat format = new DecimalFormat(whole ? "#,##0" : "#,##0.00");
        return "¥" + format.format(scaled);
    }

    public static String money(long value) {
        return money(BigDecimal.valueOf(value));
    }

    public static String number(long value) {
        return new DecimalFormat("#,##0").format(value);
    }

    public static String signedMoney(BigDecimal value, boolean income) {
        if (value == null) {
            return income ? "+¥0" : "-¥0";
        }
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        boolean whole = scaled.scale() <= 0;
        DecimalFormat format = new DecimalFormat(whole ? "#,##0" : "#,##0.00");
        return (income ? "+¥" : "-¥") + format.format(scaled);
    }

    public static String plain(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public static String oneDecimal(BigDecimal value) {
        if (value == null) {
            return "0.0";
        }
        return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
    }

    public static String oneDecimal(double value) {
        return String.format("%.1f", value);
    }

    public static String durationHm(int minutes) {
        int safe = Math.max(minutes, 0);
        return (safe / 60) + "h " + String.format("%02d", safe % 60) + "m";
    }

    public static String hoursFromMinutes(int minutes) {
        return oneDecimal(minutes / 60.0);
    }

    public static String monthName(int month) {
        return MONTH_NAMES[month - 1];
    }

    public static String monthShort(int month) {
        return month + "月";
    }

    public static String monthDay(LocalDate date) {
        return date == null ? "" : MONTH_DAY.format(date);
    }

    public static String clock(LocalDateTime at) {
        return at == null ? "" : HOUR_MINUTE.format(at);
    }

    public static String weekday(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return switch (day) {
            case MONDAY -> "星期一";
            case TUESDAY -> "星期二";
            case WEDNESDAY -> "星期三";
            case THURSDAY -> "星期四";
            case FRIDAY -> "星期五";
            case SATURDAY -> "星期六";
            case SUNDAY -> "星期日";
        };
    }

    public static String weekdayShort(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "周一";
            case TUESDAY -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY -> "周四";
            case FRIDAY -> "周五";
            case SATURDAY -> "周六";
            case SUNDAY -> "周日";
        };
    }

    public static String timeAgo(LocalDateTime at, LocalDateTime now) {
        if (at == null) {
            return "";
        }
        long minutes = Math.max(Duration.between(at, now).toMinutes(), 0);
        if (minutes < 60) {
            return "刚刚";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " 小时前";
        }
        long days = hours / 24;
        return days <= 1 ? "昨天" : days + " 天前";
    }

    public static String timeAgo(LocalDateTime at) {
        return timeAgo(at, LocalDateTime.now());
    }

    public static String transactionTime(LocalDateTime at) {
        if (at == null) {
            return "";
        }
        LocalDate today = LocalDate.now();
        if (at.toLocalDate().equals(today)) {
            return "今天 " + clock(at);
        }
        if (at.toLocalDate().equals(today.minusDays(1))) {
            return "昨天 " + clock(at);
        }
        return monthDay(at.toLocalDate());
    }

    public static String dueLabel(LocalDateTime at) {
        if (at == null) {
            return "";
        }
        LocalDate today = LocalDate.now();
        if (at.toLocalDate().equals(today)) {
            return clock(at);
        }
        if (at.toLocalDate().equals(today.plusDays(1))) {
            return "明天 " + clock(at);
        }
        return monthDay(at.toLocalDate()) + " " + clock(at);
    }

    public static String tagClass(String color) {
        if (color == null) {
            return "tag-green";
        }
        return "tag-" + color.toLowerCase();
    }

    public static String rounded(double value) {
        return String.valueOf(Math.round(value));
    }

    public static int percent(double current, double total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.min(100, Math.max(0, Math.round(current / total * 100)));
    }

    public static double percentRaw(double current, double total) {
        if (total <= 0) {
            return 0;
        }
        return Math.round(Math.min(100, Math.max(0, current / total * 100)) * 10) / 10.0;
    }
}
