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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
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
    private static final Map<Item, List<Holder<MobEffect>>> ARMOR_EFFECTS = Map.ofEntries(
            Map.entry(ModItems.XUAN_IRON_HELMET, List.of(ModEffects.BODY_TEMPERING, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.XUAN_IRON_CHESTPLATE, List.of(ModEffects.BODY_TEMPERING, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.XUAN_IRON_LEGGINGS, List.of(ModEffects.BODY_TEMPERING, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.XUAN_IRON_BOOTS, List.of(ModEffects.BODY_TEMPERING, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.SPIRIT_STONE_HELMET, List.of(ModEffects.QI_SURGE, MobEffects.REGENERATION)),
            Map.entry(ModItems.SPIRIT_STONE_CHESTPLATE, List.of(ModEffects.QI_SURGE, MobEffects.REGENERATION)),
            Map.entry(ModItems.SPIRIT_STONE_LEGGINGS, List.of(ModEffects.QI_SURGE, MobEffects.REGENERATION)),
            Map.entry(ModItems.SPIRIT_STONE_BOOTS, List.of(ModEffects.QI_SURGE, MobEffects.REGENERATION)),
            Map.entry(ModItems.MYSTIC_JADE_HELMET, List.of(ModEffects.PROTECTIVE_AURA, MobEffects.ABSORPTION)),
            Map.entry(ModItems.MYSTIC_JADE_CHESTPLATE, List.of(ModEffects.PROTECTIVE_AURA, MobEffects.ABSORPTION)),
            Map.entry(ModItems.MYSTIC_JADE_LEGGINGS, List.of(ModEffects.PROTECTIVE_AURA, MobEffects.ABSORPTION)),
            Map.entry(ModItems.MYSTIC_JADE_BOOTS, List.of(ModEffects.PROTECTIVE_AURA, MobEffects.ABSORPTION)),
            Map.entry(ModItems.STARFALL_HELMET, List.of(ModEffects.LIGHT_BODY, MobEffects.MOVEMENT_SPEED, MobEffects.SLOW_FALLING)),
            Map.entry(ModItems.STARFALL_CHESTPLATE, List.of(ModEffects.LIGHT_BODY, MobEffects.MOVEMENT_SPEED, MobEffects.SLOW_FALLING)),
            Map.entry(ModItems.STARFALL_LEGGINGS, List.of(ModEffects.LIGHT_BODY, MobEffects.MOVEMENT_SPEED, MobEffects.SLOW_FALLING)),
            Map.entry(ModItems.STARFALL_BOOTS, List.of(ModEffects.LIGHT_BODY, MobEffects.MOVEMENT_SPEED, MobEffects.SLOW_FALLING)),
            Map.entry(ModItems.HEAVENLY_DAO_HELMET, List.of(ModEffects.HEAVENLY_BLESSING, MobEffects.LUCK, MobEffects.REGENERATION)),
            Map.entry(ModItems.HEAVENLY_DAO_CHESTPLATE, List.of(ModEffects.HEAVENLY_BLESSING, MobEffects.LUCK, MobEffects.REGENERATION)),
            Map.entry(ModItems.HEAVENLY_DAO_LEGGINGS, List.of(ModEffects.HEAVENLY_BLESSING, MobEffects.LUCK, MobEffects.REGENERATION)),
            Map.entry(ModItems.HEAVENLY_DAO_BOOTS, List.of(ModEffects.HEAVENLY_BLESSING, MobEffects.LUCK, MobEffects.REGENERATION)),
            Map.entry(ModItems.HUMAN_DAO_HELMET, List.of(ModEffects.MERIT_GUARD, MobEffects.ABSORPTION, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.HUMAN_DAO_CHESTPLATE, List.of(ModEffects.MERIT_GUARD, MobEffects.ABSORPTION, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.HUMAN_DAO_LEGGINGS, List.of(ModEffects.MERIT_GUARD, MobEffects.ABSORPTION, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.HUMAN_DAO_BOOTS, List.of(ModEffects.MERIT_GUARD, MobEffects.ABSORPTION, MobEffects.DAMAGE_RESISTANCE)),
            Map.entry(ModItems.DEMON_DAO_HELMET, List.of(ModEffects.BLOOD_FRENZY, MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED)),
            Map.entry(ModItems.DEMON_DAO_CHESTPLATE, List.of(ModEffects.BLOOD_FRENZY, MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED)),
            Map.entry(ModItems.DEMON_DAO_LEGGINGS, List.of(ModEffects.BLOOD_FRENZY, MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED)),
            Map.entry(ModItems.DEMON_DAO_BOOTS, List.of(ModEffects.BLOOD_FRENZY, MobEffects.DAMAGE_BOOST, MobEffects.MOVEMENT_SPEED))
    );
    private static final Map<Item, List<EquipmentEffect>> SWORD_EFFECTS = Map.ofEntries(
            Map.entry(ModItems.XUAN_IRON_SWORD, List.of(effect(ModEffects.BODY_TEMPERING, 0), effect(MobEffects.DAMAGE_RESISTANCE, 0))),
            Map.entry(ModItems.SPIRIT_STONE_SWORD, List.of(effect(ModEffects.QI_SURGE, 0), effect(MobEffects.REGENERATION, 0))),
            Map.entry(ModItems.MYSTIC_JADE_SWORD, List.of(effect(ModEffects.PROTECTIVE_AURA, 0), effect(MobEffects.ABSORPTION, 0))),
            Map.entry(ModItems.STARFALL_SWORD, List.of(effect(ModEffects.LIGHT_BODY, 0), effect(MobEffects.MOVEMENT_SPEED, 0), effect(MobEffects.SLOW_FALLING, 0))),
            Map.entry(ModItems.FLAME_SPIRIT_SWORD, List.of(effect(ModEffects.SWORD_INTENT, 0), effect(MobEffects.FIRE_RESISTANCE, 0))),
            Map.entry(ModItems.FROST_SPIRIT_SWORD, List.of(effect(ModEffects.SWORD_INTENT, 0), effect(MobEffects.WATER_BREATHING, 0))),
            Map.entry(ModItems.THUNDER_SPIRIT_SWORD, List.of(effect(ModEffects.SWORD_INTENT, 0), effect(MobEffects.DIG_SPEED, 0))),
            Map.entry(ModItems.WIND_SPIRIT_SWORD, List.of(effect(ModEffects.SWORD_INTENT, 0), effect(MobEffects.MOVEMENT_SPEED, 0), effect(MobEffects.JUMP, 0))),
            Map.entry(ModItems.HEAVENLY_JUDGEMENT_SWORD, List.of(effect(ModEffects.HEAVENLY_BLESSING, 1), effect(MobEffects.LUCK, 1), effect(MobEffects.REGENERATION, 0))),
            Map.entry(ModItems.HUMAN_MERIT_SWORD, List.of(effect(ModEffects.MERIT_GUARD, 1), effect(MobEffects.ABSORPTION, 1), effect(MobEffects.DAMAGE_RESISTANCE, 0))),
            Map.entry(ModItems.DEMON_SOUL_SWORD, List.of(effect(ModEffects.BLOOD_FRENZY, 1), effect(MobEffects.DAMAGE_BOOST, 1), effect(MobEffects.MOVEMENT_SPEED, 0)))
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
            List<Holder<MobEffect>> effects = ARMOR_EFFECTS.get(equipped.getItem());
            if (effects != null) {
                for (Holder<MobEffect> effect : effects) {
                    armorPiecesByEffect.merge(effect, 1, Integer::sum);
                }
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
        List<EquipmentEffect> effects = SWORD_EFFECTS.get(stack.getItem());
        if (effects != null) {
            for (EquipmentEffect effect : effects) {
            addEffectToApply(effectsToApply, effect.effect(), effect.amplifier());
            }
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

    private static EquipmentEffect effect(Holder<MobEffect> effect, int amplifier) {
        return new EquipmentEffect(effect, amplifier);
    }

    private record EquipmentEffect(Holder<MobEffect> effect, int amplifier) {
    }
}
