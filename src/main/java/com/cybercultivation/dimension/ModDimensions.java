package com.cybercultivation.dimension;

import com.cybercultivation.CyberCultivationMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class ModDimensions {
    public static final ResourceKey<Level> HERBAL_SECRET_REALM = ResourceKey.create(
            Registries.DIMENSION,
            id("herbal_secret_realm")
    );

    public static final ResourceKey<Biome> HERBAL_REALM_BIOME = ResourceKey.create(
            Registries.BIOME,
            id("herbal_realm_biome")
    );

    private ModDimensions() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, path);
    }
}
