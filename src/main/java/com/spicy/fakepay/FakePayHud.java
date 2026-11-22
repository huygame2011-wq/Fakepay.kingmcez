package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class FakePayHud {
    private FakePayHud() {}

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null || mc.options.hudHidden) return;
        FakePayConfig c = FakePayConfig.get();

        String label = c.enabled ? "FakePay: ON" : "FakePay: OFF";
        int w = mc.textRenderer.getWidth(label) + 8;
        int x = Math.max(0, Math.min(c.buttonX, ctx.getScaledWindowWidth() - w));
        int y = Math.max(0, Math.min(c.buttonY, ctx.getScaledWindowHeight() - 14));
        ctx.fill(x, y, x + w, y + 14, c.enabled ? 0xCC2E7D32 : 0xCC303030);
        ctx.drawText(mc.textRenderer, label, x + 4, y + 3, 0xFFFFFFFF, true);

        if (c.fakeStatsEnabled) {
            String[] lines = {
                "$ MONEY " + c.fakeMoney,
                "* SHARD " + c.fakeShard,
                "+ KILLS " + c.fakeKills,
                "x DEATHS " + c.fakeDeaths
            };
            int[] colors = {0xFF55FF55, 0xFFAA55FF, 0xFFFF5555, 0xFFFFAA00};

            int maxW = 0;
            for (String s : lines) {
                maxW = Math.max(maxW, mc.textRenderer.getWidth(s));
            }
            int boxW = maxW + 10;
            int boxH = lines.length * 12 + 6;
            int sx = Math.max(0, Math.min(c.statsHudX, ctx.getScaledWindowWidth() - boxW));
            int sy = Math.max(0, Math.min(c.statsHudY, ctx.getScaledWindowHeight() - boxH));

            ctx.fill(sx, sy, sx + boxW, sy + boxH, 0xCC1E1E1E);
            for (int i = 0; i < lines.length; i++) {
                ctx.drawText(mc.textRenderer, lines[i], sx + 5, sy + 4 + i * 12, colors[i], true);
            }
        }
    }
}
