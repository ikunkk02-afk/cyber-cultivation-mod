package com.cybercultivation.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FlyingSwordVisualState {
    private static final Map<Integer, ResourceLocation> FLYING_SWORDS = new ConcurrentHashMap<>();

    private FlyingSwordVisualState() {
    }

    public static void set(int entityId, boolean flyingSword, ResourceLocation itemId) {
        if (flyingSword && itemId != null) {
            FLYING_SWORDS.put(entityId, itemId);
        } else {
            FLYING_SWORDS.remove(entityId);
        }
    }

    public static ResourceLocation get(int entityId) {
        return FLYING_SWORDS.get(entityId);
    }

    public static void clear() {
        FLYING_SWORDS.clear();
    }
}
