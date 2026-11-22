package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class DragToggle {
    private static boolean dragging = false;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static boolean wasMouseDown = false;

    private DragToggle() {}

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.currentScreen != null) {
            dragging = false;
            wasMouseDown = false;
            return;
        }

        FakePayConfig cfg = FakePayConfig.get();
        if (!cfg.enabled) {
            return;
        }

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        int w = 78;
        int h = 18;

        int x = cfg.buttonX;
        int y = cfg.buttonY;
        if (x < 0) x = 10;
        if (y < 0) y = 10;
        if (x + w > sw) x = Math.max(0, sw - w);
        if (y + h > sh) y = Math.max(0, sh - h);

        double mouseX = mc.mouse.getX() * sw / Math.max(1, mc.getWindow().getWidth());
        double mouseY = mc.mouse.getY() * sh / Math.max(1, mc.getWindow().getHeight());
        boolean mouseDown = org.lwjgl.glfw.GLFW.glfwGetMouseButton(
                mc.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        if (mouseDown && !wasMouseDown && hovered) {
            dragging = true;
            dragOffsetX = (int) (mouseX - x);
            dragOffsetY = (int) (mouseY - y);
        } else if (!mouseDown && dragging) {
            dragging = false;
            cfg.buttonX = x;
            cfg.buttonY = y;
            FakePayConfig.get().save();
        }

        if (dragging) {
            x = Math.max(0, Math.min(sw - w, (int) (mouseX - dragOffsetX)));
            y = Math.max(0, Math.min(sh - h, (int) (mouseY - dragOffsetY)));
            cfg.buttonX = x;
            cfg.buttonY = y;
        }

        wasMouseDown = mouseDown;

        int bg = dragging ? 0xDD2E1047 : (hovered ? 0xCC1E142D : 0xAA120D1A);
        int border = dragging || hovered ? 0xFF9D4EDD : 0xFF5A189A;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.fill(x, y, x + w, y + 1, border);
        ctx.fill(x, y + h - 1, x + w, y + h, border);
        ctx.fill(x, y, x + 1, y + h, border);
        ctx.fill(x + w - 1, y, x + w, y + h, border);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, "FakePay", x + w / 2, y + 5, 0xFFFFFFFF);
    }
}
