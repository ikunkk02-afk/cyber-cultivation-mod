package com.cybercultivation.block;

import com.cybercultivation.CyberCultivationMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
    public static final Block CULTIVATION_ALTAR = register(
            "cultivation_altar",
            new CultivationAltarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_PURPLE)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST))
    );

    private ModBlocks() {
    }

    public static void registerModBlocks() {
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation blocks");
    }

    private static Block register(String name, Block block) {
        return Registry.register(
                BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name),
                block
        );
    }
}
