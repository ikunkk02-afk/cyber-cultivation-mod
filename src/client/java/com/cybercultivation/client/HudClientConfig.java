package com.cybercultivation.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HudClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("cyber-cultivation-client.json");
    private static final float MIN_SCALE = 0.5F;
    private static final float MAX_SCALE = 2.0F;

    private int hudX = 10;
    private int hudY = 10;
    private float hudScale = 1.0F;

    public int getHudX() {
        return hudX;
    }

    public void setHudX(int hudX) {
        this.hudX = Math.max(0, hudX);
    }

    public int getHudY() {
        return hudY;
    }

    public void setHudY(int hudY) {
        this.hudY = Math.max(0, hudY);
    }

    public float getHudScale() {
        return hudScale;
    }

    public void setHudScale(float hudScale) {
        if (Float.isNaN(hudScale) || Float.isInfinite(hudScale)) {
            this.hudScale = 1.0F;
            return;
        }
        this.hudScale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, hudScale));
    }

    public static HudClientConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            HudClientConfig config = new HudClientConfig();
            config.save();
            return config;
        }
        try {
            String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            HudClientConfig config = GSON.fromJson(json, HudClientConfig.class);
            if (config == null) {
                config = new HudClientConfig();
            }
            config.sanitize();
            config.save();
            return config;
        } catch (IOException | JsonSyntaxException exception) {
            HudClientConfig config = new HudClientConfig();
            config.save();
            return config;
        }
    }

    public void save() {
        sanitize();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private void sanitize() {
        setHudX(hudX);
        setHudY(hudY);
        setHudScale(hudScale);
    }
}
