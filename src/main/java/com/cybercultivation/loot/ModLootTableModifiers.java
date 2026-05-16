package com.cybercultivation.loot;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.entity.ModEntities;
import com.cybercultivation.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class ModLootTableModifiers {
    private ModLootTableModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            addMonsterDrops(key, tableBuilder);
        });

        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation loot table modifiers");
    }

    private static void addMonsterDrops(ResourceKey<LootTable> key, LootTable.Builder tableBuilder) {
        if (matches(key, EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.14F);
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.07F);
            addChanceDrop(tableBuilder, ModItems.QI_RECOVERY_PILL, 0.025F);
        } else if (matches(key, EntityType.SKELETON, EntityType.STRAY, EntityType.BOGGED)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.13F);
            addChanceDrop(tableBuilder, ModItems.TALISMAN_PAPER, 0.10F);
            addChanceDrop(tableBuilder, ModItems.ARRAY_CORE, 0.025F);
        } else if (matches(key, EntityType.SPIDER, EntityType.CAVE_SPIDER)) {
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.12F);
            addChanceDrop(tableBuilder, ModItems.DEMON_BLOOD_VINE, 0.06F);
        } else if (matches(key, EntityType.CREEPER)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.14F);
            addChanceDrop(tableBuilder, ModItems.ARRAY_CORE, 0.08F);
        } else if (matches(key, EntityType.ENDERMAN)) {
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.22F);
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.10F);
            addChanceDrop(tableBuilder, ModItems.MYSTIC_JADE_SWORD, 0.01F);
        } else if (matches(key, EntityType.BLAZE, EntityType.MAGMA_CUBE)) {
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.20F);
            addChanceDrop(tableBuilder, ModItems.ALCHEMY_ESSENCE, 0.12F);
            addChanceDrop(tableBuilder, ModItems.FLAME_FLOWER, 0.08F);
        } else if (matches(key, EntityType.WITCH)) {
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.18F);
            addChanceDrop(tableBuilder, ModItems.ALCHEMY_ESSENCE, 0.12F);
            addChanceDrop(tableBuilder, ModItems.QI_RECOVERY_PILL, 0.08F);
            addChanceDrop(tableBuilder, ModItems.HEART_CLEANSING_PILL, 0.025F);
        } else if (matches(key, EntityType.WITHER_SKELETON)) {
            addChanceDrop(tableBuilder, ModItems.DEMON_CORE, 0.22F);
            addChanceDrop(tableBuilder, ModItems.HIGH_GRADE_SPIRIT_STONE, 0.12F);
            addChanceDrop(tableBuilder, ModItems.SOUL_LANTERN_FLOWER, 0.07F);
        } else if (matches(key, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.14F);
            addChanceDrop(tableBuilder, ModItems.TALISMAN_PAPER, 0.11F);
            addChanceDrop(tableBuilder, ModItems.ARRAY_CORE, 0.04F);
        } else if (matches(key, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN)) {
            if (EntityType.ELDER_GUARDIAN.getDefaultLootTable().equals(key)) {
                addGuaranteedDrop(tableBuilder, ModItems.HIGH_GRADE_SPIRIT_STONE);
            }
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.28F);
            addChanceDrop(tableBuilder, ModItems.FROST_FLOWER, 0.10F);
        } else if (matches(key, EntityType.SLIME)) {
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.10F);
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.08F);
        } else if (matches(key, EntityType.PHANTOM)) {
            addChanceDrop(tableBuilder, ModItems.SOUL_LANTERN_FLOWER, 0.12F);
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.08F);
        } else if (matches(key, EntityType.RAVAGER)) {
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.35F);
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.18F);
        } else if (matches(key, ModEntities.SPIRIT_DEER)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.18F);
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.12F);
        } else if (matches(key, ModEntities.SPIRIT_CRANE)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.14F);
            addChanceDrop(tableBuilder, ModItems.MOONLIGHT_LOTUS, 0.08F);
        } else if (matches(key, ModEntities.STONE_SHELL_TURTLE)) {
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.30F);
            addChanceDrop(tableBuilder, ModItems.EARTH_ROOT_GINSENG, 0.08F);
        } else if (matches(key, ModEntities.DEMON_WOLF)) {
            addChanceDrop(tableBuilder, ModItems.DEMON_CORE, 0.35F);
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.18F);
            addChanceDrop(tableBuilder, ModItems.DEMON_BLOOD_VINE, 0.10F);
        }
    }

    private static boolean matches(ResourceKey<LootTable> key, EntityType<?>... types) {
        for (EntityType<?> type : types) {
            if (type.getDefaultLootTable().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private static void addGuaranteedDrop(LootTable.Builder tableBuilder, ItemLike item) {
        tableBuilder.withPool(dropPool(item));
    }

    private static void addChanceDrop(LootTable.Builder tableBuilder, ItemLike item, float chance) {
        tableBuilder.withPool(dropPool(item)
                .when(LootItemRandomChanceCondition.randomChance(chance)));
    }

    private static LootPool.Builder dropPool(ItemLike item) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item));
    }
}
