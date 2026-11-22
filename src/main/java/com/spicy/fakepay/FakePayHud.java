package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.glfw.GLFW;

public final class FakePayHud {
    private static boolean draggingStats = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static boolean wasMouseDown = false;

    private FakePayHud() {}

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.options.hudHidden) {
            draggingStats = false;
            wasMouseDown = false;
            return;
        }

        FakePayConfig c = FakePayConfig.get();
        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();

        String label = c.enabled ? "FakePay: ON" : "FakePay: OFF";
        int w = mc.textRenderer.getWidth(label) + 8;
        int x = Math.max(0, Math.min(c.buttonX, sw - w));
        int y = Math.max(0, Math.min(c.buttonY, sh - 14));
        ctx.fill(x, y, x + w, y + 14, c.enabled ? 0xCC2E7D32 : 0xCC303030);
        ctx.drawText(mc.textRenderer, label, x + 4, y + 3, 0xFFFFFFFF, true);

        if (!c.fakeStatsEnabled) {
            draggingStats = false;
            wasMouseDown = false;
            return;
        }

        String[] lines = {
            "$ MONEY " + c.fakeMoney,
            "* SHARD " + c.fakeShard,
            "+ KILLS " + c.fakeKills,
            "x DEATHS " + c.fakeDeaths
        };
        int[] colors = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};

        int maxW = 0;
        for (String s : lines) {
            maxW = Math.max(maxW, mc.textRenderer.getWidth(s));
        }
        int boxW = maxW + 10;
        int boxH = lines.length * 12 + 6;
        int sx = Math.max(0, Math.min(c.statsHudX, sw - boxW));
        int sy = Math.max(0, Math.min(c.statsHudY, sh - boxH));

        double mouseX = mc.mouse.getX() * sw / Math.max(1, mc.getWindow().getWidth());
        double mouseY = mc.mouse.getY() * sh / Math.max(1, mc.getWindow().getHeight());
        boolean mouseDown = GLFW.glfwGetMouseButton(
                mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean hovered = mouseX >= sx && mouseX <= sx + boxW && mouseY >= sy && mouseY <= sy + boxH;

        if (mouseDown && !wasMouseDown && hovered) {
            draggingStats = true;
            dragOffsetX = (int) (mouseX - sx);
            dragOffsetY = (int) (mouseY - sy);
        } else if (!mouseDown && draggingStats) {
            draggingStats = false;
            c.statsHudX = sx;
            c.statsHudY = sy;
            c.save();
        }

        if (draggingStats) {
            sx = Math.max(0, Math.min(sw - boxW, (int) (mouseX - dragOffsetX)));
            sy = Math.max(0, Math.min(sh - boxH, (int) (mouseY - dragOffsetY)));
            c.statsHudX = sx;
            c.statsHudY = sy;
        }

        wasMouseDown = mouseDown;

        int bgColor = draggingStats ? 0xDD2E1047 : (hovered ? 0xCC2A2A2A : 0xCC1E1E1E);
        ctx.fill(sx, sy, sx + boxW, sy + boxH, bgColor);
        if (draggingStats || hovered) {
            int border = draggingStats ? 0xFF9D4EDD : 0xFF888888;
            ctx.fill(sx, sy, sx + boxW, sy + 1, border);
            ctx.fill(sx, sy + boxH - 1, sx + boxW, sy + boxH, border);
            ctx.fill(sx, sy, sx + 1, sy + boxH, border);
            ctx.fill(sx + boxW - 1, sy, sx + boxW, sy + boxH, border);
        }

        for (int i = 0; i < lines.length; i++) {
            ctx.drawText(mc.textRenderer, lines[i], sx + 5, sy + 4 + i * 12, colors[i], true);
        }
    }
}
