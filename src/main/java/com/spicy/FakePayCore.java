package com.spicy.fakepay;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logic thuần Java: kiểm tra đầu vào và dựng tin nhắn cục bộ.
 * Không gọi Minecraft, không mạng, không gửi gì lên server.
 */
public final class FakePayCore {
    private static final Pattern NAME = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private static final Pattern AMOUNT = Pattern.compile("^(\\d{1,12})(\\.\\d{1,2})?([kKmMbBtT])?$");

    public record Result(boolean ok, String message) {}

    private FakePayCore() {}

    /** true nếu chuỗi lệnh (không có dấu '/') bắt đầu bằng "pay". */
    public static boolean isPayCommand(String command) {
        if (command == null) return false;
        String c = command.trim();
        return c.equalsIgnoreCase("pay") || c.toLowerCase(Locale.ROOT).startsWith("pay ");
    }

    /** Xử lý lệnh dạng "pay <tên> <số tiền>". */
    public static Result fromCommand(String command) {
        String[] t = command == null ? new String[0] : command.trim().split("\\s+");
        if (t.length != 3 || !t[0].equalsIgnoreCase("pay")) {
            return new Result(false, "[FakePay] Cú pháp: /pay <tên> <số tiền>  (ví dụ: /pay SoloplayerVN 20B)");
        }
        return preview(t[1], t[2]);
    }

    /** Kiểm tra tên + số tiền rồi dựng tin nhắn. */
    public static Result preview(String name, String amount) {
        String n = name == null ? "" : name.trim();
        String a = amount == null ? "" : amount.trim();
        if (!NAME.matcher(n).matches()) {
            return new Result(false, "[FakePay] Tên không hợp lệ (3-16 ký tự: chữ, số, dấu gạch dưới).");
        }
        Matcher m = AMOUNT.matcher(a);
        if (!m.matches()) {
            return new Result(false, "[FakePay] Số tiền không hợp lệ. Ví dụ: 500, 1.5K, 20B.");
        }
        String normalized = m.group(1) + (m.group(2) == null ? "" : m.group(2))
                + (m.group(3) == null ? "" : m.group(3).toUpperCase(Locale.ROOT));
        return new Result(true, "Bạn đã chuyển cho " + n + " $" + normalized);
    }
}
