package com.cybercultivation.item;

import com.cybercultivation.item.custom.DaoRestrictedArmorItem;
import com.cybercultivation.item.custom.DaoRestrictedSwordItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class DaoEquipmentHandler {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private DaoEquipmentHandler() {
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide && DaoRestrictedSwordItem.isRestrictedWrongPath(player, player.getItemInHand(hand))) {
                player.displayClientMessage(DaoRestrictedSwordItem.WRONG_PATH_MESSAGE, true);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            enforceArmorRestrictions(player);
        }
    }

    public static void enforceArmorRestrictions(ServerPlayer player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack equipped = player.getItemBySlot(slot);
            if (equipped.isEmpty() || DaoRestrictedArmorItem.isAllowedFor(player, equipped)) {
                continue;
            }
            ItemStack removed = equipped.copy();
            player.setItemSlot(slot, ItemStack.EMPTY);
            if (!player.getInventory().add(removed)) {
                player.drop(removed, false);
            }
            player.displayClientMessage(DaoRestrictedArmorItem.WRONG_PATH_MESSAGE, true);
        }
    }
}
