package com.cybercultivation.mixin;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.flysword.FlyingSwordHandler;
import com.cybercultivation.meditation.MeditationHandler;
import com.cybercultivation.realm.RealmManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DamageMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof Player player) {
            PlayerQiData data = PlayerQiManager.get(player);
            if (data != null && data.isMeditating()) {
                MeditationHandler.exitMeditation(player);
            }
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer && data != null && data.isFlyingSword()) {
                FlyingSwordHandler.exitSilently(serverPlayer);
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            FlyingSwordHandler.exitSilently(serverPlayer);
            RealmManager.clearForDeath(serverPlayer);
        }
    }
}
