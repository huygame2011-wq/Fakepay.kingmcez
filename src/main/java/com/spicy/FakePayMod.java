package com.spicy.fakepay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakePayMod implements ClientModInitializer {
    public static final String ID = "fakepay";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        FakePayConfig.load();

        // Phím mở bảng điều khiển: mặc định J (không dùng G/M), đổi được trong Controls.
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fakepay.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new FakePayScreen());
            }
        });

        // Chặn lệnh /pay CHỈ khi module đang ON. Lệnh KHÔNG được gửi lên server.
        ClientSendMessageEvents.ALLOW_COMMAND.register(FakePayCommandHandler::allow);

        HudRenderCallback.EVENT.register(FakePayHud::render);
        LOGGER.info("FakePay đã tải.");
    }

    public static MinecraftClient mc() { return MinecraftClient.getInstance(); }
}
