package com.cybercultivation.util;

import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;

public final class FlyingSwordHelper {
    private static final Set<Item> FLYING_SWORDS = Set.of(
            ModItems.FLYING_SWORD,
            ModItems.XUAN_IRON_SWORD,
            ModItems.SPIRIT_STONE_SWORD,
            ModItems.MYSTIC_JADE_SWORD,
            ModItems.STARFALL_SWORD,
            ModItems.FLAME_SPIRIT_SWORD,
            ModItems.FROST_SPIRIT_SWORD,
            ModItems.THUNDER_SPIRIT_SWORD,
            ModItems.WIND_SPIRIT_SWORD,
            ModItems.HEAVENLY_JUDGEMENT_SWORD,
            ModItems.HUMAN_MERIT_SWORD,
            ModItems.DEMON_SOUL_SWORD
    );

    private static final Map<Item, CultivationPath> DAO_RESTRICTED_SWORDS = Map.of(
            ModItems.HEAVENLY_JUDGEMENT_SWORD, CultivationPath.HEAVENLY_DAO,
            ModItems.HUMAN_MERIT_SWORD, CultivationPath.HUMAN_DAO,
            ModItems.DEMON_SOUL_SWORD, CultivationPath.DEMON_DAO
    );

    private FlyingSwordHelper() {
    }

    public static ItemStack getHeldFlyingSword(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (isFlyingSword(mainHand)) {
            return mainHand;
        }

        ItemStack offhand = player.getOffhandItem();
        if (isFlyingSword(offhand)) {
            return offhand;
        }

        return ItemStack.EMPTY;
    }

    public static boolean isFlyingSword(ItemStack stack) {
        return !stack.isEmpty() && FLYING_SWORDS.contains(stack.getItem());
    }

    public static boolean canPathUseSword(CultivationPath path, ItemStack stack) {
        CultivationPath requiredPath = DAO_RESTRICTED_SWORDS.get(stack.getItem());
        return requiredPath == null || requiredPath == path;
    }

    public static ResourceLocation getItemId(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }
}
