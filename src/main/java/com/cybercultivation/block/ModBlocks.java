package com.cybercultivation.block;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.block.custom.AlchemyCauldronBlock;
import com.cybercultivation.block.custom.CultivationFlowerBlock;
import com.cybercultivation.block.custom.SpiritHerbCropBlock;
import com.cybercultivation.block.custom.SpiritRiceCropBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
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
    public static final Block CULTIVATION_TABLE = register(
            "cultivation_table",
            new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F, 3.5F)
                    .sound(SoundType.WOOD))
    );
    public static final Block ALCHEMY_CAULDRON = register(
            "alchemy_cauldron",
            new AlchemyCauldronBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 4))
    );
    public static final Block SPIRIT_HERB_CROP = register(
            "spirit_herb_crop",
            new SpiritHerbCropBlock(cropProperties())
    );
    public static final Block SPIRIT_RICE_CROP = register(
            "spirit_rice_crop",
            new SpiritRiceCropBlock(cropProperties())
    );
    public static final Block FLAME_FLOWER = registerFlower("flame_flower", MapColor.COLOR_ORANGE);
    public static final Block FROST_FLOWER = registerFlower("frost_flower", MapColor.ICE);
    public static final Block THUNDER_GRASS = registerFlower("thunder_grass", MapColor.COLOR_LIGHT_BLUE);
    public static final Block DEMON_BLOOD_VINE = registerFlower("demon_blood_vine", MapColor.COLOR_RED);
    public static final Block MOONLIGHT_LOTUS = registerFlower("moonlight_lotus", MapColor.ICE, 4);
    public static final Block GOLDEN_SPIRIT_BAMBOO = registerFlower("golden_spirit_bamboo", MapColor.GOLD);
    public static final Block EARTH_ROOT_GINSENG = registerFlower("earth_root_ginseng", MapColor.DIRT);
    public static final Block SOUL_LANTERN_FLOWER = registerFlower("soul_lantern_flower", MapColor.COLOR_PURPLE, 5);
    public static final Block FIVE_ELEMENT_FRUIT = registerFlower("five_element_fruit", MapColor.COLOR_GREEN);

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

    private static Block registerFlower(String name, MapColor mapColor) {
        return registerFlower(name, mapColor, 0);
    }

    private static Block registerFlower(String name, MapColor mapColor, int lightLevel) {
        return register(name, new CultivationFlowerBlock(MobEffects.REGENERATION, 6.0F, BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .lightLevel(state -> lightLevel)
                .offsetType(BlockBehaviour.OffsetType.XZ)));
    }

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP);
    }
}
