package com.cybercultivation.villager;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.block.ModBlocks;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

public final class ModVillagers {
    public static final ResourceLocation CULTIVATION_MERCHANT_ID = id("cultivation_merchant");
    public static final ResourceLocation CULTIVATION_TABLE_POI_ID = id("cultivation_table");
    public static final ResourceKey<PoiType> CULTIVATION_TABLE_POI_KEY = ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            CULTIVATION_TABLE_POI_ID
    );

    public static final PoiType CULTIVATION_TABLE_POI = PointOfInterestHelper.register(
            CULTIVATION_TABLE_POI_ID,
            1,
            1,
            ModBlocks.CULTIVATION_TABLE
    );

    public static final VillagerProfession CULTIVATION_MERCHANT = Registry.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            CULTIVATION_MERCHANT_ID,
            new VillagerProfession(
                    CULTIVATION_MERCHANT_ID.getPath(),
                    entry -> entry.is(CULTIVATION_TABLE_POI_KEY),
                    entry -> entry.is(CULTIVATION_TABLE_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_CLERIC
            )
    );

    private ModVillagers() {
    }

    public static void registerModVillagers() {
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation villagers");
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name);
    }
}
