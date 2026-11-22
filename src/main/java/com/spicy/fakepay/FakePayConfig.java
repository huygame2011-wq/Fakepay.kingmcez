package com.spicy.fakepay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class FakePayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("fakepay.json");

    private static FakePayConfig instance;

    public boolean enabled = true;
    public int buttonX = 12;
    public int buttonY = 12;

    public boolean fakeStatsEnabled = false;
    public String fakeMoney = "3.7B";
    public String fakeShard = "265";
    public String fakeKills = "11";
    public String fakeDeaths = "22";
    public int statsHudX = 12;
    public int statsHudY = 30;

    public static FakePayConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static FakePayConfig load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                FakePayConfig cfg = GSON.fromJson(r, FakePayConfig.class);
                if (cfg != null) {
                    if (cfg.fakeMoney == null || cfg.fakeMoney.isBlank()) cfg.fakeMoney = "3.7B";
                    if (cfg.fakeShard == null || cfg.fakeShard.isBlank()) cfg.fakeShard = "265";
                    if (cfg.fakeKills == null || cfg.fakeKills.isBlank()) cfg.fakeKills = "11";
                    if (cfg.fakeDeaths == null || cfg.fakeDeaths.isBlank()) cfg.fakeDeaths = "22";
                    return cfg;
                }
            } catch (Exception ignored) {
            }
        }
        FakePayConfig cfg = new FakePayConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
        } catch (Exception ignored) {
        }
    }
}
