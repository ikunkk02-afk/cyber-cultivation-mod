package com.cybercultivation.handler;

import com.cybercultivation.network.AttackEffectPayload;
import com.cybercultivation.util.FlyingSwordHelper;
import com.cybercultivation.util.ParticleColorHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class AttackEffectHandler {
    private AttackEffectHandler() {
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            if (!FlyingSwordHelper.isFlyingSword(stack)) return InteractionResult.PASS;

            ResourceLocation itemId = FlyingSwordHelper.getItemId(stack);
            AttackEffectPayload payload = new AttackEffectPayload(player.getId(), itemId);
            ServerLevel serverLevel = (ServerLevel) world;
            for (ServerPlayer p : serverLevel.players()) {
                ServerPlayNetworking.send(p, payload);
            }

            spawnSwordQiParticles(serverLevel, player.getEyePosition(), player.getLookAngle(), entity, stack);

            return InteractionResult.PASS;
        });
    }

    private static void spawnSwordQiParticles(ServerLevel level, Vec3 origin, Vec3 look, Entity target, ItemStack stack) {
        Vector3f color = ParticleColorHelper.getSwordQiColor(stack);
        DustParticleOptions bladeOptions = new DustParticleOptions(color, 1.15F);
        DustParticleOptions sparkOptions = new DustParticleOptions(new Vector3f(0.90F, 1.00F, 1.00F), 0.55F);
        Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.55, 0);
        Vec3 right = look.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-4D) {
            right = new Vec3(1, 0, 0);
        } else {
            right = right.normalize();
        }
        Vec3 up = right.cross(look).normalize();

        for (int i = 0; i < 18; i++) {
            double progress = (double) i / 17.0D;
            double arc = Math.sin(progress * Math.PI);
            Vec3 pos = origin
                    .add(look.scale(0.45D + progress * 1.75D))
                    .add(right.scale((progress - 0.5D) * 2.0D))
                    .add(up.scale(arc * 0.42D));
            level.sendParticles(bladeOptions,
                    pos.x, pos.y, pos.z,
                    1, look.x * 0.04D, look.y * 0.04D, look.z * 0.04D, 0.02D);
        }

        for (int i = 0; i < 14; i++) {
            double angle = i * Math.PI * 0.45D;
            double radius = 0.25D + level.getRandom().nextDouble() * 0.75D;
            Vec3 pos = hitPos
                    .add(right.scale(Math.cos(angle) * radius))
                    .add(up.scale(Math.sin(angle) * radius));
            level.sendParticles(sparkOptions,
                    pos.x, pos.y, pos.z,
                    1, (level.getRandom().nextDouble() - 0.5D) * 0.12D, 0.06D,
                    (level.getRandom().nextDouble() - 0.5D) * 0.12D, 0.04D);
        }

        level.sendParticles(ParticleTypes.END_ROD,
                hitPos.x, hitPos.y, hitPos.z,
                6, 0.35D, 0.25D, 0.35D, 0.03D);
    }
}
