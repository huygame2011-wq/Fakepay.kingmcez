package com.spicy.fakepay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class FakePayScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private TextFieldWidget amountField;
    private String statusText = "";
    private int statusColor = 0xFFAAAAAA;

    public FakePayScreen(Screen parent) {
        super(Text.literal("FakePay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        FakePayConfig cfg = FakePayConfig.get();
        int cx = this.width / 2;
        int top = Math.max(24, this.height / 2 - 85);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(cfg.enabled ? "FakePay: ON" : "FakePay: OFF"),
                btn -> {
                    cfg.enabled = !cfg.enabled;
                    cfg.save();
                    btn.setMessage(Text.literal(cfg.enabled ? "FakePay: ON" : "FakePay: OFF"));
                }
        ).dimensions(cx - 100, top, 200, 20).build());

        nameField = new TextFieldWidget(this.textRenderer, cx - 100, top + 26, 200, 20, Text.literal("Tên"));
        nameField.setMaxLength(16);
        nameField.setPlaceholder(Text.literal("Tên (vd: SoloplayerVN)"));
        this.addDrawableChild(nameField);

        amountField = new TextFieldWidget(this.textRenderer, cx - 100, top + 52, 200, 20, Text.literal("Số tiền"));
        amountField.setMaxLength(16);
        amountField.setPlaceholder(Text.literal("Số tiền (vd: 20B)"));
        this.addDrawableChild(amountField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Gửi Pay giả"), btn -> {
            FakePayCore.Result r = FakePayCore.preview(nameField.getText(), amountField.getText());
            statusText = r.message();
            statusColor = r.ok() ? 0xFF55FF55 : 0xFFFF5555;
            if (r.ok()) {
                FakePayCommandHandler.show(r);
            }
        }).dimensions(cx - 100, top + 78, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Mở menu Fake Stats"), btn -> {
            if (this.client != null) {
                this.client.setScreen(new FakeStatsScreen(this));
            }
        }).dimensions(cx - 100, top + 106, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Đóng"), btn -> close())
                .dimensions(cx - 100, top + 132, 200, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int top = Math.max(24, this.height / 2 - 85);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "FakePay Menu", cx, top - 14, 0xFFFFFFFF);
        if (!statusText.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer, statusText, cx, top + 160, statusColor);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
