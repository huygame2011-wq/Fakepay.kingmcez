package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class FakePayCommandHandler {
    private FakePayCommandHandler() {}

    /** @return true = cho lệnh đi tiếp lên server; false = đã xử lý cục bộ, không gửi. */
    public static boolean allow(String command) {
        FakePayConfig cfg = FakePayConfig.get();
        if (!cfg.enabled || !FakePayCore.isPayCommand(command)) return true;
        show(FakePayCore.fromCommand(command));
        return false;
    }

    /** Hiển thị trong khung chat cục bộ (chỉ bạn thấy). */
    public static void show(FakePayCore.Result r) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        String m = r.message();
        MutableText out;
        if (r.ok() && m.startsWith("Bạn đã chuyển cho ")) {
            String[] p = m.split(" ");
            String money = p[p.length - 1];
            String name = p[p.length - 2];
            out = Text.literal("Bạn đã chuyển cho ").formatted(Formatting.WHITE)
                    .append(Text.literal(name).formatted(Formatting.YELLOW))
                    .append(Text.literal(" "))
                    .append(Text.literal(money).formatted(Formatting.GREEN));
        } else {
            out = Text.literal(m).formatted(Formatting.RED);
        }
        mc.player.sendMessage(out, false);
    }
}
