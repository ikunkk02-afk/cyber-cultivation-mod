package com.cybercultivation.worldgen;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.dimension.ModDimensions;
import com.cybercultivation.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntitySpawns {
    private ModEntitySpawns() {
    }

    public static void register() {
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST, Biomes.PLAINS, Biomes.CHERRY_GROVE),
                MobCategory.CREATURE,
                ModEntities.SPIRIT_DEER,
                8,
                2,
                4
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.MEADOW, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.RIVER, Biomes.CHERRY_GROVE),
                MobCategory.CREATURE,
                ModEntities.SPIRIT_CRANE,
                6,
                1,
                3
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.DARK_FOREST),
                MobCategory.MONSTER,
                ModEntities.DEMON_WOLF,
                18,
                1,
                3
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.RIVER, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BEACH, Biomes.SNOWY_BEACH),
                MobCategory.CREATURE,
                ModEntities.STONE_SHELL_TURTLE,
                5,
                1,
                2
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModDimensions.HERBAL_REALM_BIOME),
                MobCategory.CREATURE,
                ModEntities.SPIRIT_DEER,
                16,
                2,
                4
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModDimensions.HERBAL_REALM_BIOME),
                MobCategory.CREATURE,
                ModEntities.SPIRIT_CRANE,
                12,
                1,
                3
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModDimensions.HERBAL_REALM_BIOME),
                MobCategory.CREATURE,
                ModEntities.STONE_SHELL_TURTLE,
                8,
                1,
                2
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModDimensions.HERBAL_REALM_BIOME),
                MobCategory.MONSTER,
                ModEntities.DEMON_WOLF,
                35,
                1,
                4
        );
        SpawnPlacements.register(ModEntities.SPIRIT_DEER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.SPIRIT_CRANE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.DEMON_WOLF, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, spawnType, pos, random) -> level.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(level, pos, random)
                        && Mob.checkMobSpawnRules(type, level, spawnType, pos, random));
        SpawnPlacements.register(ModEntities.STONE_SHELL_TURTLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation entity spawns");
    }
}
