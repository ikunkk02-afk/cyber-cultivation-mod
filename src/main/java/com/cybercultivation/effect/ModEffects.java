package com.cybercultivation.effect;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ModEffects {
    public static final Holder<MobEffect> QI_SURGE = register("qi_surge",
            new QiSurgeEffect(MobEffectCategory.BENEFICIAL, 0x28D8F5)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("qi_surge_speed"), 0.05D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> QI_EXHAUSTION = register("qi_exhaustion",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x263746)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("qi_exhaustion_speed"), -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, id("qi_exhaustion_attack_speed"), -0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MINING_EFFICIENCY, id("qi_exhaustion_mining"), -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> PROTECTIVE_AURA = register("protective_aura",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF4D982)
                    .addAttributeModifier(Attributes.ARMOR, id("protective_aura_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, id("protective_aura_toughness"), 1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> ARMOR_BREAKING_SHA = register("armor_breaking_sha",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x73111C)
                    .addAttributeModifier(Attributes.ARMOR, id("armor_breaking_sha_armor"), -3.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, id("armor_breaking_sha_toughness"), -1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> LIGHT_BODY = register("light_body",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x91F7F0)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("light_body_speed"), 0.12D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, id("light_body_safe_fall"), 3.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.STEP_HEIGHT, id("light_body_step"), 0.35D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> HEAVENLY_BLESSING = register("heavenly_blessing",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xFFF1B8)
                    .addAttributeModifier(Attributes.LUCK, id("heavenly_blessing_luck"), 1.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("heavenly_blessing_damage"), 1.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR, id("heavenly_blessing_armor"), 1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> MERIT_GUARD = register("merit_guard",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF6C642)
                    .addAttributeModifier(Attributes.ARMOR, id("merit_guard_armor"), 2.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id("merit_guard_knockback"), 0.10D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.MAX_HEALTH, id("merit_guard_health"), 2.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> BLOOD_FRENZY = register("blood_frenzy",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xD71935)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("blood_frenzy_damage"), 2.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, id("blood_frenzy_speed"), 0.08D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.ARMOR, id("blood_frenzy_armor_cost"), -1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> SWORD_INTENT = register("sword_intent",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xDCE8F2)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("sword_intent_damage"), 1.5D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, id("sword_intent_speed"), 0.10D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder<MobEffect> BODY_TEMPERING = register("body_tempering",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xB87333)
                    .addAttributeModifier(Attributes.MAX_HEALTH, id("body_tempering_health"), 4.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, id("body_tempering_toughness"), 2.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id("body_tempering_knockback"), 0.15D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> ALCHEMY_BREATH = register("alchemy_breath",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x9A67C7)
                    .addAttributeModifier(Attributes.LUCK, id("alchemy_breath_luck"), 1.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.OXYGEN_BONUS, id("alchemy_breath_oxygen"), 1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> TALISMAN_BLESSING = register("talisman_blessing",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF8D94A)
                    .addAttributeModifier(Attributes.LUCK, id("talisman_blessing_luck"), 1.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR, id("talisman_blessing_armor"), 1.0D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> FORMATION_GUARD = register("formation_guard",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x6555D9)
                    .addAttributeModifier(Attributes.ARMOR, id("formation_guard_armor"), 3.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, id("formation_guard_knockback"), 0.15D, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder<MobEffect> MEDICAL_REGENERATION = register("medical_regeneration",
            new MedicalRegenerationEffect(MobEffectCategory.BENEFICIAL, 0x46D66C));
    public static final Holder<MobEffect> INNER_DEMON = register("inner_demon",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x211027)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, id("inner_demon_damage"), 2.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.MAX_HEALTH, id("inner_demon_health"), -4.0D, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ARMOR, id("inner_demon_armor"), -2.0D, AttributeModifier.Operation.ADD_VALUE));

    private ModEffects() {
    }

    public static void registerModEffects() {
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation mob effects");
    }

    private static Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                id(name),
                effect
        );
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name);
    }

    private static class CultivationMobEffect extends MobEffect {
        protected CultivationMobEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    private static final class QiSurgeEffect extends CultivationMobEffect {
        private QiSurgeEffect(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return duration % 20 == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
            if (livingEntity instanceof ServerPlayer player) {
                PlayerQiData data = PlayerQiManager.getOrCreate(player);
                int before = data.getCurrentQi();
                data.setCurrentQi(before + 1 + amplifier);
                if (data.getCurrentQi() != before) {
                    PlayerQiManager.syncToClient(player);
                }
            }
            return true;
        }
    }

    private static final class MedicalRegenerationEffect extends CultivationMobEffect {
        private MedicalRegenerationEffect(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            int interval = Math.max(10, 50 >> amplifier);
            return duration % interval == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                livingEntity.heal(1.0F);
            }
            return true;
        }
    }
}
