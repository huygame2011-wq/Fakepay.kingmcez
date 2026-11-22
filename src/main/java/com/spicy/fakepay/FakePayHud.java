package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Vẽ nhãn trạng thái nhỏ tại vị trí nút. Vài lệnh vẽ mỗi frame, không tạo đối tượng nặng. */
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
    }
}
