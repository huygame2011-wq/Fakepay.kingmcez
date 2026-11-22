package com.spicy.fakepay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

/** Nút "FakePay" ON/OFF: chạm/click = bật tắt, kéo = di chuyển. */
public class DragToggle extends ClickableWidget {
    private boolean dragged;
    private float fx, fy;

    public DragToggle(int x, int y, int w, int h) {
        super(x, y, w, h, Text.literal("FakePay"));
        fx = x; fy = y;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean on = FakePayConfig.get().enabled;
        int bg = on ? 0xFF2E7D32 : 0xFF7A1F1F;
        if (isHovered()) bg = on ? 0xFF388E3C : 0xFF9A2A2A;
        ctx.fill(getX(), getY(), getX() + width, getY() + height, bg);
        ctx.fill(getX(), getY(), getX() + width, getY() + 1, 0xFFFFFFFF);
        String t = "FakePay: " + (on ? "ON" : "OFF");
        var tr = MinecraftClient.getInstance().textRenderer;
        ctx.drawText(tr, t, getX() + (width - tr.getWidth(t)) / 2, getY() + (height - 8) / 2, 0xFFFFFFFF, true);
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        dragged = false;
        fx = getX(); fy = getY();
    }

    @Override
    protected void onDrag(Click click, double dx, double dy) {
        if (Math.abs(dx) + Math.abs(dy) > 0.01) dragged = true;
        var win = MinecraftClient.getInstance().getWindow();
        fx = (float) Math.max(0, Math.min(fx + dx, win.getScaledWidth() - width));
        fy = (float) Math.max(0, Math.min(fy + dy, win.getScaledHeight() - height));
        setX(Math.round(fx));
        setY(Math.round(fy));
    }

    @Override
    public void onRelease(Click click) {
        FakePayConfig c = FakePayConfig.get();
        if (dragged) {
            c.buttonX = getX();
            c.buttonY = getY();
        } else {
            c.enabled = !c.enabled;
        }
        FakePayConfig.save();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        appendDefaultNarrations(builder);
    }
}
