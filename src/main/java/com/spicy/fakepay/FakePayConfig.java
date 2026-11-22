package com.spicy.fakepay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Cấu hình lưu tại .minecraft/config/fakepay.json. Chỉ ghi khi đổi trạng thái, không ghi mỗi frame. */
public final class FakePayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fakepay.json");

    public boolean enabled = false;
    public boolean minimized = false;
    public int buttonX = 10;
    public int buttonY = 60;
    public String lastName = "";
    public String lastAmount = "";

    private static FakePayConfig instance = new FakePayConfig();

    public static FakePayConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                FakePayConfig c = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), FakePayConfig.class);
                if (c != null) instance = c;
            }
        } catch (Exception e) {
            FakePayMod.LOGGER.warn("Không đọc được fakepay.json, dùng mặc định: {}", e.toString());
            instance = new FakePayConfig();
        }
        if (instance.lastName == null) instance.lastName = "";
        if (instance.lastAmount == null) instance.lastAmount = "";
    }

    public static void save() {
        try {
            Files.writeString(FILE, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException e) {
            FakePayMod.LOGGER.warn("Không ghi được fakepay.json: {}", e.toString());
        }
    }
}
