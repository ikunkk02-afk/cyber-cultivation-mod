package com.cybercultivation.villager;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

public final class ModTrades {
    private static final float PRICE_MULTIPLIER = 0.05F;

    private ModTrades() {
    }

    public static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(ModVillagers.CULTIVATION_MERCHANT, 1, factories -> {
            factories.add(sellForEmeralds(ModItems.LOW_GRADE_SPIRIT_STONE, 2, 1, 16, 2));
            factories.add(sellForEmeralds(ModItems.SPIRIT_HERB, 1, 3, 12, 2));
            factories.add(sellForEmeralds(ModItems.TALISMAN_PAPER, 4, 2, 12, 2));
            factories.add(sellForItem(ModItems.LOW_GRADE_SPIRIT_STONE, 5, ModItems.QI_RECOVERY_PILL, 1, 10, 3));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.CULTIVATION_MERCHANT, 2, factories -> {
            factories.add(sellForEmeralds(ModItems.SPIRIT_STONE, 1, 6, 10, 5));
            factories.add(sellForEmeralds(ModItems.QI_GATHERING_PILL, 1, 8, 8, 5));
            factories.add(sellForEmeralds(ModItems.SPIRIT_PEACH, 1, 5, 12, 4));
            factories.add(sellForEmeralds(ModItems.ARRAY_CORE, 1, 10, 8, 6));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.CULTIVATION_MERCHANT, 3, factories -> {
            factories.add(sellForEmeralds(ModItems.MIDDLE_GRADE_SPIRIT_STONE, 1, 12, 8, 10));
            factories.add(sellForEmeralds(ModItems.HEALING_PILL, 1, 15, 6, 10));
            factories.add(sellForEmeralds(ModItems.DETOX_PILL, 1, 18, 6, 10));
            factories.add(sellForEmeralds(ModItems.XUAN_IRON_SWORD, 1, 20, 4, 12));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.CULTIVATION_MERCHANT, 4, factories -> {
            factories.add(sellForEmeralds(ModItems.HIGH_GRADE_SPIRIT_STONE, 1, 24, 6, 15));
            factories.add(sellForEmeralds(ModItems.FOUNDATION_PILL, 1, 28, 4, 15));
            factories.add(sellForEmeralds(ModItems.HEART_CLEANSING_PILL, 1, 30, 4, 15));
            factories.add(sellForEmeralds(ModItems.SPIRIT_STONE_SWORD, 1, 32, 3, 18));
        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.CULTIVATION_MERCHANT, 5, factories -> {
            factories.add(sellForEmeralds(ModItems.FLYING_SWORD, 1, 40, 2, 30));
            factories.add(sellForEmeralds(ModItems.MYSTIC_JADE_SWORD, 1, 48, 2, 30));
            factories.add(sellForEmeralds(ModItems.STARFALL_SWORD, 1, 56, 2, 30));
            factories.add(randomAdvancedSword(2, 30));
        });

        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation villager trades");
    }

    private static VillagerTrades.ItemListing sellForEmeralds(ItemLike result, int resultCount, int emeraldCost, int maxUses, int xp) {
        return sellForItem(Items.EMERALD, emeraldCost, result, resultCount, maxUses, xp);
    }

    private static VillagerTrades.ItemListing sellForItem(ItemLike cost, int costCount, ItemLike result, int resultCount, int maxUses, int xp) {
        return (entity, random) -> new MerchantOffer(
                new ItemCost(cost, costCount),
                new ItemStack(result, resultCount),
                maxUses,
                xp,
                PRICE_MULTIPLIER
        );
    }

    private static VillagerTrades.ItemListing randomAdvancedSword(int maxUses, int xp) {
        Item[] swords = {
                ModItems.FLAME_SPIRIT_SWORD,
                ModItems.FROST_SPIRIT_SWORD,
                ModItems.THUNDER_SPIRIT_SWORD,
                ModItems.WIND_SPIRIT_SWORD
        };
        return (Entity entity, RandomSource random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 64),
                Optional.of(new ItemCost(ModItems.HIGH_GRADE_SPIRIT_STONE, 1)),
                new ItemStack(swords[random.nextInt(swords.length)]),
                maxUses,
                xp,
                PRICE_MULTIPLIER
        );
    }
}
