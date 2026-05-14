package com.cybercultivation.effect;

import com.cybercultivation.CyberCultivationMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class ModEffects {
    public static final Holder<MobEffect> QI_SURGE = register("qi_surge",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x28D8F5));
    public static final Holder<MobEffect> QI_EXHAUSTION = register("qi_exhaustion",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x263746));
    public static final Holder<MobEffect> PROTECTIVE_AURA = register("protective_aura",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF4D982));
    public static final Holder<MobEffect> ARMOR_BREAKING_SHA = register("armor_breaking_sha",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x73111C));
    public static final Holder<MobEffect> LIGHT_BODY = register("light_body",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x91F7F0));
    public static final Holder<MobEffect> HEAVENLY_BLESSING = register("heavenly_blessing",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xFFF1B8));
    public static final Holder<MobEffect> MERIT_GUARD = register("merit_guard",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF6C642));
    public static final Holder<MobEffect> BLOOD_FRENZY = register("blood_frenzy",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xD71935));
    public static final Holder<MobEffect> SWORD_INTENT = register("sword_intent",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xDCE8F2));
    public static final Holder<MobEffect> BODY_TEMPERING = register("body_tempering",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xB87333));
    public static final Holder<MobEffect> ALCHEMY_BREATH = register("alchemy_breath",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x9A67C7));
    public static final Holder<MobEffect> TALISMAN_BLESSING = register("talisman_blessing",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0xF8D94A));
    public static final Holder<MobEffect> FORMATION_GUARD = register("formation_guard",
            new CultivationMobEffect(MobEffectCategory.BENEFICIAL, 0x6555D9));
    public static final Holder<MobEffect> MEDICAL_REGENERATION = register("medical_regeneration",
            new MedicalRegenerationEffect(MobEffectCategory.BENEFICIAL, 0x46D66C));
    public static final Holder<MobEffect> INNER_DEMON = register("inner_demon",
            new CultivationMobEffect(MobEffectCategory.HARMFUL, 0x211027));

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
