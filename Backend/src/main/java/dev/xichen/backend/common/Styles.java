package dev.xichen.backend.common;

/**
 * 前端样式映射：服务端不存储颜色，按稳定的规则给出 pill / bar 样式名。
 */
public final class Styles {
    private Styles() {
    }

    public static String tagPill(String tag) {
        if (tag == null) {
            return "low";
        }
        return switch (tag) {
            case "产品", "认知", "跑步", "设计" -> "blue";
            case "财务", "随笔", "旅行", "2025" -> "mid";
            case "灵感", "影评" -> "pink";
            default -> "low";
        };
    }

    public static String categoryPill(String category) {
        if (category == null) {
            return "mid";
        }
        return switch (category) {
            case "餐饮", "收入", "其他" -> "low";
            case "交通" -> "blue";
            case "娱乐" -> "pink";
            default -> "mid";
        };
    }

    public static String barColor(String color) {
        return color == null ? "green" : color;
    }

    public static String folderIcon(String name) {
        if (name == null) {
            return "📁";
        }
        if (name.startsWith("00")) {
            return "📥";
        }
        if (name.startsWith("10")) {
            return "📝";
        }
        if (name.startsWith("20")) {
            return "🚀";
        }
        if (name.startsWith("30")) {
            return "🗄️";
        }
        if (name.contains("读书")) {
            return "📖";
        }
        if (name.contains("产品")) {
            return "💡";
        }
        if (name.contains("生活")) {
            return "🌿";
        }
        return "📁";
    }

    public static String mediaLabel(String mediaType) {
        if (mediaType == null) {
            return "其他";
        }
        return switch (mediaType) {
            case "book" -> "图书";
            case "movie" -> "电影";
            case "series" -> "剧集";
            case "documentary" -> "纪录片";
            case "anime" -> "动画";
            default -> "其他";
        };
    }

    public static String mediaPill(String mediaType) {
        if (mediaType == null) {
            return "low";
        }
        return switch (mediaType) {
            case "movie" -> "low";
            case "series" -> "blue";
            case "documentary" -> "mid";
            case "book", "anime" -> "pink";
            default -> "low";
        };
    }

    public static String rotateBar(int index) {
        String[] palette = {"green", "blue", "yellow", "pink"};
        return palette[Math.floorMod(index, palette.length)];
    }
}
