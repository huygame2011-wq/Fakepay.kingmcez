package com.spicy.fakepay;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class FakePayMod implements ClientModInitializer {
    public static final String MOD_ID = "fakepay";
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        FakePayConfig.get();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fakepay.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new FakePayScreen(null));
                }
            }
        });

        HudRenderCallback.EVENT.register((ctx, tick) -> {
            DragToggle.render(ctx, tick);
            FakePayHud.render(ctx, tick);
        });
    }
}
