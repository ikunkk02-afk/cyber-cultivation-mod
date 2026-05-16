package com.cybercultivation.item;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CultivationPillItem extends Item {
    private final int qiRestore;
    private final List<PillEffect> effects;
    private final List<Holder<MobEffect>> removedEffects;

    public CultivationPillItem(Properties properties,
                               int qiRestore,
                               List<PillEffect> effects,
                               List<Holder<MobEffect>> removedEffects) {
        super(properties);
        this.qiRestore = qiRestore;
        this.effects = List.copyOf(effects);
        this.removedEffects = List.copyOf(removedEffects);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide) {
            for (Holder<MobEffect> effect : removedEffects) {
                livingEntity.removeEffect(effect);
            }
            for (PillEffect effect : effects) {
                livingEntity.addEffect(new MobEffectInstance(
                        effect.effect(),
                        effect.durationTicks(),
                        effect.amplifier(),
                        false,
                        true,
                        true
                ));
            }
            if (qiRestore > 0 && livingEntity instanceof ServerPlayer player) {
                PlayerQiData data = PlayerQiManager.getOrCreate(player);
                data.setCurrentQi(data.getCurrentQi() + qiRestore);
                PlayerQiManager.syncToClient(player);
            }
        }
        return result;
    }

    public record PillEffect(Holder<MobEffect> effect, int durationTicks, int amplifier) {
    }
}
