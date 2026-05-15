package com.cybercultivation.worldgen;

import com.cybercultivation.CyberCultivationMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModWorldGeneration {
    private static final ResourceKey<PlacedFeature> SPIRIT_HERB_PATCH = placedFeature("spirit_herb_patch");
    private static final ResourceKey<PlacedFeature> SPIRIT_RICE_PATCH = placedFeature("spirit_rice_patch");
    private static final ResourceKey<PlacedFeature> FLAME_FLOWER_PATCH = placedFeature("flame_flower_patch");
    private static final ResourceKey<PlacedFeature> FROST_FLOWER_PATCH = placedFeature("frost_flower_patch");
    private static final ResourceKey<PlacedFeature> THUNDER_GRASS_PATCH = placedFeature("thunder_grass_patch");
    private static final ResourceKey<PlacedFeature> DEMON_BLOOD_VINE_PATCH = placedFeature("demon_blood_vine_patch");
    private static final ResourceKey<PlacedFeature> MOONLIGHT_LOTUS_PATCH = placedFeature("moonlight_lotus_patch");
    private static final ResourceKey<PlacedFeature> GOLDEN_SPIRIT_BAMBOO_PATCH = placedFeature("golden_spirit_bamboo_patch");
    private static final ResourceKey<PlacedFeature> EARTH_ROOT_GINSENG_PATCH = placedFeature("earth_root_ginseng_patch");
    private static final ResourceKey<PlacedFeature> EARTH_ROOT_GINSENG_CAVE_PATCH = placedFeature("earth_root_ginseng_cave_patch");
    private static final ResourceKey<PlacedFeature> SOUL_LANTERN_FLOWER_PATCH = placedFeature("soul_lantern_flower_patch");
    private static final ResourceKey<PlacedFeature> SOUL_LANTERN_FLOWER_CAVE_PATCH = placedFeature("soul_lantern_flower_cave_patch");
    private static final ResourceKey<PlacedFeature> FIVE_ELEMENT_FRUIT_PATCH = placedFeature("five_element_fruit_patch");

    private ModWorldGeneration() {
    }

    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.PLAINS, Biomes.CHERRY_GROVE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                SPIRIT_HERB_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.RIVER, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.PLAINS),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                SPIRIT_RICE_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS,
                        Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.BASALT_DELTAS),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                FLAME_FLOWER_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.GROVE,
                        Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                FROST_FLOWER_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.MEADOW, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST,
                        Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                THUNDER_GRASS_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.DEEP_DARK,
                        Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                DEMON_BLOOD_VINE_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.RIVER),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                MOONLIGHT_LOTUS_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.BAMBOO_JUNGLE, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.CHERRY_GROVE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                GOLDEN_SPIRIT_BAMBOO_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.MEADOW),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                EARTH_ROOT_GINSENG_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                EARTH_ROOT_GINSENG_CAVE_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.SOUL_SAND_VALLEY),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                SOUL_LANTERN_FLOWER_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DEEP_DARK, Biomes.LUSH_CAVES),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                SOUL_LANTERN_FLOWER_CAVE_PATCH
        );
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.CHERRY_GROVE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                FIVE_ELEMENT_FRUIT_PATCH
        );
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation world generation");
    }

    private static ResourceKey<PlacedFeature> placedFeature(String name) {
        return ResourceKey.create(
                Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name)
        );
    }
}
