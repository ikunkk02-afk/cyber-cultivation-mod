package com.cybercultivation.loot;

import com.cybercultivation.CyberCultivationMod;
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
        if (EntityType.ZOMBIE.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.10F);
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.05F);
        } else if (EntityType.SKELETON.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.10F);
            addChanceDrop(tableBuilder, ModItems.TALISMAN_PAPER, 0.08F);
        } else if (EntityType.SPIDER.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.08F);
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.05F);
        } else if (EntityType.CREEPER.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.12F);
            addChanceDrop(tableBuilder, ModItems.ARRAY_CORE, 0.05F);
        } else if (EntityType.ENDERMAN.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.20F);
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.08F);
        } else if (EntityType.BLAZE.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.MIDDLE_GRADE_SPIRIT_STONE, 0.20F);
            addChanceDrop(tableBuilder, ModItems.ALCHEMY_ESSENCE, 0.10F);
        } else if (EntityType.WITCH.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.SPIRIT_HERB, 0.15F);
            addChanceDrop(tableBuilder, ModItems.ALCHEMY_ESSENCE, 0.10F);
            addChanceDrop(tableBuilder, ModItems.QI_RECOVERY_PILL, 0.05F);
        } else if (EntityType.WITHER_SKELETON.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.DEMON_CORE, 0.20F);
            addChanceDrop(tableBuilder, ModItems.HIGH_GRADE_SPIRIT_STONE, 0.10F);
        } else if (EntityType.PILLAGER.getDefaultLootTable().equals(key)) {
            addChanceDrop(tableBuilder, ModItems.LOW_GRADE_SPIRIT_STONE, 0.12F);
            addChanceDrop(tableBuilder, ModItems.TALISMAN_PAPER, 0.08F);
        } else if (EntityType.ELDER_GUARDIAN.getDefaultLootTable().equals(key)) {
            addGuaranteedDrop(tableBuilder, ModItems.HIGH_GRADE_SPIRIT_STONE);
            addChanceDrop(tableBuilder, ModItems.BEAST_CORE, 0.30F);
        }
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
