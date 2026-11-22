package com.spicy.fakepay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class FakeStatsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget moneyField;
    private TextFieldWidget shardField;
    private TextFieldWidget killsField;
    private TextFieldWidget deathsField;
    private ButtonWidget toggleBtn;
    private String statusText = "";
    private int statusColor = 0xFFAAAAAA;

    public FakeStatsScreen(Screen parent) {
        super(Text.literal("Fake Stats"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        FakePayConfig cfg = FakePayConfig.get();
        int cx = this.width / 2;
        int top = Math.max(20, this.height / 2 - 95);

        toggleBtn = ButtonWidget.builder(
                Text.literal(cfg.fakeStatsEnabled ? "Fake Stats: ON" : "Fake Stats: OFF"),
                btn -> {
                    cfg.fakeStatsEnabled = !cfg.fakeStatsEnabled;
                    cfg.save();
                    btn.setMessage(Text.literal(cfg.fakeStatsEnabled ? "Fake Stats: ON" : "Fake Stats: OFF"));
                }
        ).dimensions(cx - 100, top, 200, 20).build();
        this.addDrawableChild(toggleBtn);

        moneyField = new TextFieldWidget(this.textRenderer, cx - 100, top + 28, 96, 20, Text.literal("Money"));
        moneyField.setMaxLength(16);
        moneyField.setText(cfg.fakeMoney);
        moneyField.setPlaceholder(Text.literal("Money"));
        this.addDrawableChild(moneyField);

        shardField = new TextFieldWidget(this.textRenderer, cx + 4, top + 28, 96, 20, Text.literal("Shard"));
        shardField.setMaxLength(16);
        shardField.setText(cfg.fakeShard);
        shardField.setPlaceholder(Text.literal("Shard"));
        this.addDrawableChild(shardField);

        killsField = new TextFieldWidget(this.textRenderer, cx - 100, top + 54, 96, 20, Text.literal("Kills"));
        killsField.setMaxLength(16);
        killsField.setText(cfg.fakeKills);
        killsField.setPlaceholder(Text.literal("Kills"));
        this.addDrawableChild(killsField);

        deathsField = new TextFieldWidget(this.textRenderer, cx + 4, top + 54, 96, 20, Text.literal("Deaths"));
        deathsField.setMaxLength(16);
        deathsField.setText(cfg.fakeDeaths);
        deathsField.setPlaceholder(Text.literal("Deaths"));
        this.addDrawableChild(deathsField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Áp dụng chỉ số giả"), btn -> {
            cfg.fakeMoney = moneyField.getText().trim().isEmpty() ? "3.7B" : moneyField.getText().trim();
            cfg.fakeShard = shardField.getText().trim().isEmpty() ? "265" : shardField.getText().trim();
            cfg.fakeKills = killsField.getText().trim().isEmpty() ? "11" : killsField.getText().trim();
            cfg.fakeDeaths = deathsField.getText().trim().isEmpty() ? "22" : deathsField.getText().trim();
            cfg.fakeStatsEnabled = true;
            cfg.save();
            toggleBtn.setMessage(Text.literal("Fake Stats: ON"));
            statusText = "Đã lưu chỉ số giả cục bộ.";
            statusColor = 0xFF55FF55;
        }).dimensions(cx - 100, top + 82, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Quay lại"), btn -> close())
                .dimensions(cx - 100, top + 108, 200, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int top = Math.max(20, this.height / 2 - 95);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "Menu Fake Stats (Client Only)", cx, top - 14, 0xFFFFFFFF);
        if (!statusText.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer, statusText, cx, top + 136, statusColor);
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
