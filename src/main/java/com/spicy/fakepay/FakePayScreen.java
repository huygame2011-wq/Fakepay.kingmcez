package com.spicy.fakepay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class FakePayScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget amountField;
    private String feedback = "";
    private boolean feedbackOk = true;

    public FakePayScreen() {
        super(Text.literal("FakePay"));
    }

    @Override
    protected void init() {
        FakePayConfig c = FakePayConfig.get();
        int pw = Math.min(width - 20, 230);
        int px = (width - pw) / 2;
        int py = Math.max(10, height / 2 - 70);

        addDrawableChild(ButtonWidget.builder(Text.literal(c.minimized ? "+" : "-"), b -> {
            c.minimized = !c.minimized;
            FakePayConfig.save();
            clearAndInit();
        }).dimensions(px + pw - 22, py + 2, 20, 16).build());

        if (!c.minimized) {
            nameField = new TextFieldWidget(textRenderer, px + 8, py + 36, pw - 16, 20, Text.literal("Tên"));
            nameField.setMaxLength(16);
            nameField.setPlaceholder(Text.literal("Tên người nhận"));
            nameField.setText(c.lastName);
            nameField.setChangedListener(s -> c.lastName = s);
            addDrawableChild(nameField);

            amountField = new TextFieldWidget(textRenderer, px + 8, py + 62, pw - 16, 20, Text.literal("Số tiền"));
            amountField.setMaxLength(16);
            amountField.setPlaceholder(Text.literal("Số tiền (vd: 20B)"));
            amountField.setText(c.lastAmount);
            amountField.setChangedListener(s -> c.lastAmount = s);
            addDrawableChild(amountField);

            addDrawableChild(ButtonWidget.builder(Text.literal("Preview"), b -> doPreview())
                    .dimensions(px + 8, py + 88, pw - 16, 20).build());
        }

        // Thêm sau cùng để nằm trên cùng khi kéo.
        addDrawableChild(new DragToggle(c.buttonX, c.buttonY, 90, 20));
    }

    private void doPreview() {
        FakePayCore.Result r = FakePayCore.preview(nameField.getText(), amountField.getText());
        feedback = r.message();
        feedbackOk = r.ok();
        FakePayCommandHandler.show(r);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Không làm mờ nền để tránh lỗi blur nhiều lần trong 1 frame.
        ctx.fill(0, 0, width, height, 0x66000000);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        FakePayConfig c = FakePayConfig.get();
        int pw = Math.min(width - 20, 230);
        int px = (width - pw) / 2;
        int py = Math.max(10, height / 2 - 70);
        int ph = c.minimized ? 20 : 130;

        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(px, py, px + pw, py + ph, 0xEE15101F);
        ctx.fill(px, py, px + pw, py + 20, 0xFF5B2A86);
        ctx.drawText(textRenderer, "FakePay", px + 6, py + 6, 0xFFFFFFFF, true);

        if (!c.minimized) {
            String hint = c.enabled ? "ON: /pay hiển thị cục bộ, không gửi lên server"
                                    : "OFF: /pay hoạt động bình thường";
            ctx.drawText(textRenderer, hint, px + 8, py + 24, 0xFFBBBBBB, false);
            if (!feedback.isEmpty()) {
                ctx.drawText(textRenderer, feedback, px + 8, py + 114, feedbackOk ? 0xFFFFDD55 : 0xFFFF5555, false);
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        FakePayConfig.save();
    }
}
