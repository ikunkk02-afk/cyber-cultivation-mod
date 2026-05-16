package com.cybercultivation.block;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.block.entity.AlchemyCauldronBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<AlchemyCauldronBlockEntity> ALCHEMY_CAULDRON = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, "alchemy_cauldron"),
            BlockEntityType.Builder.of(AlchemyCauldronBlockEntity::new, ModBlocks.ALCHEMY_CAULDRON).build(null)
    );

    private ModBlockEntities() {
    }

    public static void registerBlockEntities() {
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation block entities");
    }
}
