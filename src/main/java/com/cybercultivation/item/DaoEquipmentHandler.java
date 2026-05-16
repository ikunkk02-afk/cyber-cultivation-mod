package com.cybercultivation.item;

import com.cybercultivation.effect.ModEffects;
import com.cybercultivation.item.custom.DaoRestrictedArmorItem;
import com.cybercultivation.item.custom.DaoRestrictedSwordItem;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class DaoEquipmentHandler {
    private static final int EFFECT_REFRESH_INTERVAL_TICKS = 20;
    private static final int EQUIPMENT_EFFECT_DURATION_TICKS = 60;
    private static final int ATTACK_EFFECT_DURATION_TICKS = 100;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };
    private static final Map<Item, Holder<MobEffect>> ARMOR_EFFECTS = Map.ofEntries(
            Map.entry(ModItems.XUAN_IRON_HELMET, ModEffects.BODY_TEMPERING),
            Map.entry(ModItems.XUAN_IRON_CHESTPLATE, ModEffects.BODY_TEMPERING),
            Map.entry(ModItems.XUAN_IRON_LEGGINGS, ModEffects.BODY_TEMPERING),
            Map.entry(ModItems.XUAN_IRON_BOOTS, ModEffects.BODY_TEMPERING),
            Map.entry(ModItems.SPIRIT_STONE_HELMET, ModEffects.QI_SURGE),
            Map.entry(ModItems.SPIRIT_STONE_CHESTPLATE, ModEffects.QI_SURGE),
            Map.entry(ModItems.SPIRIT_STONE_LEGGINGS, ModEffects.QI_SURGE),
            Map.entry(ModItems.SPIRIT_STONE_BOOTS, ModEffects.QI_SURGE),
            Map.entry(ModItems.MYSTIC_JADE_HELMET, ModEffects.PROTECTIVE_AURA),
            Map.entry(ModItems.MYSTIC_JADE_CHESTPLATE, ModEffects.PROTECTIVE_AURA),
            Map.entry(ModItems.MYSTIC_JADE_LEGGINGS, ModEffects.PROTECTIVE_AURA),
            Map.entry(ModItems.MYSTIC_JADE_BOOTS, ModEffects.PROTECTIVE_AURA),
            Map.entry(ModItems.STARFALL_HELMET, ModEffects.LIGHT_BODY),
            Map.entry(ModItems.STARFALL_CHESTPLATE, ModEffects.LIGHT_BODY),
            Map.entry(ModItems.STARFALL_LEGGINGS, ModEffects.LIGHT_BODY),
            Map.entry(ModItems.STARFALL_BOOTS, ModEffects.LIGHT_BODY),
            Map.entry(ModItems.HEAVENLY_DAO_HELMET, ModEffects.HEAVENLY_BLESSING),
            Map.entry(ModItems.HEAVENLY_DAO_CHESTPLATE, ModEffects.HEAVENLY_BLESSING),
            Map.entry(ModItems.HEAVENLY_DAO_LEGGINGS, ModEffects.HEAVENLY_BLESSING),
            Map.entry(ModItems.HEAVENLY_DAO_BOOTS, ModEffects.HEAVENLY_BLESSING),
            Map.entry(ModItems.HUMAN_DAO_HELMET, ModEffects.MERIT_GUARD),
            Map.entry(ModItems.HUMAN_DAO_CHESTPLATE, ModEffects.MERIT_GUARD),
            Map.entry(ModItems.HUMAN_DAO_LEGGINGS, ModEffects.MERIT_GUARD),
            Map.entry(ModItems.HUMAN_DAO_BOOTS, ModEffects.MERIT_GUARD),
            Map.entry(ModItems.DEMON_DAO_HELMET, ModEffects.BLOOD_FRENZY),
            Map.entry(ModItems.DEMON_DAO_CHESTPLATE, ModEffects.BLOOD_FRENZY),
            Map.entry(ModItems.DEMON_DAO_LEGGINGS, ModEffects.BLOOD_FRENZY),
            Map.entry(ModItems.DEMON_DAO_BOOTS, ModEffects.BLOOD_FRENZY)
    );
    private static final Map<Item, EquipmentEffect> SWORD_EFFECTS = Map.ofEntries(
            Map.entry(ModItems.XUAN_IRON_SWORD, new EquipmentEffect(ModEffects.BODY_TEMPERING, 0)),
            Map.entry(ModItems.SPIRIT_STONE_SWORD, new EquipmentEffect(ModEffects.QI_SURGE, 0)),
            Map.entry(ModItems.MYSTIC_JADE_SWORD, new EquipmentEffect(ModEffects.PROTECTIVE_AURA, 0)),
            Map.entry(ModItems.STARFALL_SWORD, new EquipmentEffect(ModEffects.LIGHT_BODY, 0)),
            Map.entry(ModItems.FLAME_SPIRIT_SWORD, new EquipmentEffect(ModEffects.SWORD_INTENT, 0)),
            Map.entry(ModItems.FROST_SPIRIT_SWORD, new EquipmentEffect(ModEffects.SWORD_INTENT, 0)),
            Map.entry(ModItems.THUNDER_SPIRIT_SWORD, new EquipmentEffect(ModEffects.SWORD_INTENT, 0)),
            Map.entry(ModItems.WIND_SPIRIT_SWORD, new EquipmentEffect(ModEffects.SWORD_INTENT, 0)),
            Map.entry(ModItems.HEAVENLY_JUDGEMENT_SWORD, new EquipmentEffect(ModEffects.HEAVENLY_BLESSING, 1)),
            Map.entry(ModItems.HUMAN_MERIT_SWORD, new EquipmentEffect(ModEffects.MERIT_GUARD, 1)),
            Map.entry(ModItems.DEMON_SOUL_SWORD, new EquipmentEffect(ModEffects.BLOOD_FRENZY, 1))
    );

    private DaoEquipmentHandler() {
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide && DaoRestrictedSwordItem.isRestrictedWrongPath(player, player.getItemInHand(hand))) {
                player.displayClientMessage(DaoRestrictedSwordItem.WRONG_PATH_MESSAGE, true);
                return InteractionResult.FAIL;
            }
            if (!world.isClientSide && entity instanceof LivingEntity target) {
                applySwordAttackEffects(player.getItemInHand(hand), target);
            }
            return InteractionResult.PASS;
        });
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            enforceArmorRestrictions(player);
            if (player.tickCount % EFFECT_REFRESH_INTERVAL_TICKS == 0) {
                refreshEquipmentEffects(player);
            }
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

    private static void refreshEquipmentEffects(ServerPlayer player) {
        Map<Holder<MobEffect>, Integer> armorPiecesByEffect = new HashMap<>();
        Map<Holder<MobEffect>, Integer> effectsToApply = new HashMap<>();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack equipped = player.getItemBySlot(slot);
            Holder<MobEffect> effect = ARMOR_EFFECTS.get(equipped.getItem());
            if (effect != null) {
                armorPiecesByEffect.merge(effect, 1, Integer::sum);
            }
        }

        for (Map.Entry<Holder<MobEffect>, Integer> entry : armorPiecesByEffect.entrySet()) {
            int amplifier = entry.getValue() >= ARMOR_SLOTS.length ? 1 : 0;
            addEffectToApply(effectsToApply, entry.getKey(), amplifier);
        }

        addHeldSwordEffect(effectsToApply, player.getMainHandItem());
        addHeldSwordEffect(effectsToApply, player.getOffhandItem());

        for (Map.Entry<Holder<MobEffect>, Integer> entry : effectsToApply.entrySet()) {
            applyEquipmentEffect(player, entry.getKey(), entry.getValue());
        }
    }

    private static void addHeldSwordEffect(Map<Holder<MobEffect>, Integer> effectsToApply, ItemStack stack) {
        EquipmentEffect effect = SWORD_EFFECTS.get(stack.getItem());
        if (effect != null) {
            addEffectToApply(effectsToApply, effect.effect(), effect.amplifier());
        }
    }

    private static void addEffectToApply(Map<Holder<MobEffect>, Integer> effectsToApply,
                                         Holder<MobEffect> effect,
                                         int amplifier) {
        effectsToApply.merge(effect, amplifier, Math::max);
    }

    private static void applySwordAttackEffects(ItemStack stack, LivingEntity target) {
        if (stack.is(ModItems.DEMON_SOUL_SWORD)) {
            target.addEffect(new MobEffectInstance(ModEffects.ARMOR_BREAKING_SHA, ATTACK_EFFECT_DURATION_TICKS, 0, false, true, true));
        }
    }

    private static void applyEquipmentEffect(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, EQUIPMENT_EFFECT_DURATION_TICKS, amplifier, true, true, true));
    }

    private record EquipmentEffect(Holder<MobEffect> effect, int amplifier) {
    }
}
