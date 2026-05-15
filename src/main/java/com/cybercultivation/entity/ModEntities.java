package com.cybercultivation.entity;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.entity.custom.DemonWolfEntity;
import com.cybercultivation.entity.custom.SpiritCraneEntity;
import com.cybercultivation.entity.custom.SpiritDeerEntity;
import com.cybercultivation.entity.custom.StoneShellTurtleEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    public static final EntityType<SpiritDeerEntity> SPIRIT_DEER = register(
            "spirit_deer",
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.CREATURE)
                    .entityFactory(SpiritDeerEntity::new)
                    .dimensions(EntityDimensions.scalable(0.9F, 1.4F))
                    .trackRangeBlocks(8)
                    .build()
    );
    public static final EntityType<SpiritCraneEntity> SPIRIT_CRANE = register(
            "spirit_crane",
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.CREATURE)
                    .entityFactory(SpiritCraneEntity::new)
                    .dimensions(EntityDimensions.scalable(0.55F, 1.3F))
                    .trackRangeBlocks(8)
                    .build()
    );
    public static final EntityType<DemonWolfEntity> DEMON_WOLF = register(
            "demon_wolf",
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.MONSTER)
                    .entityFactory(DemonWolfEntity::new)
                    .dimensions(EntityDimensions.scalable(0.8F, 0.9F))
                    .trackRangeBlocks(8)
                    .build()
    );
    public static final EntityType<StoneShellTurtleEntity> STONE_SHELL_TURTLE = register(
            "stone_shell_turtle",
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.CREATURE)
                    .entityFactory(StoneShellTurtleEntity::new)
                    .dimensions(EntityDimensions.scalable(1.1F, 0.55F))
                    .trackRangeBlocks(8)
                    .build()
    );
    private ModEntities() {
    }

    public static void registerModEntities() {
        FabricDefaultAttributeRegistry.register(SPIRIT_DEER, SpiritDeerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SPIRIT_CRANE, SpiritCraneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(DEMON_WOLF, DemonWolfEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(STONE_SHELL_TURTLE, StoneShellTurtleEntity.createAttributes());
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation entities");
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType<T> entityType) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name),
                entityType
        );
    }
}
