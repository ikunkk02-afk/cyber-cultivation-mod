package com.cybercultivation.handler;

import com.cybercultivation.network.AttackEffectPayload;
import com.cybercultivation.util.FlyingSwordHelper;
import com.cybercultivation.util.ParticleColorHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
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

            Vec3 hitPos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
            spawnSwordQiParticles(serverLevel, hitPos, stack);

            return InteractionResult.PASS;
        });
    }

    private static void spawnSwordQiParticles(ServerLevel level, Vec3 pos, ItemStack stack) {
        Vector3f color = ParticleColorHelper.getSwordQiColor(stack);
        DustParticleOptions options = new DustParticleOptions(color, 1.0F);
        for (int i = 0; i < 8; i++) {
            double ox = (level.random.nextDouble() - 0.5) * 0.8;
            double oy = (level.random.nextDouble() - 0.5) * 0.6 + 1.0;
            double oz = (level.random.nextDouble() - 0.5) * 0.8;
            level.sendParticles(options,
                    pos.x + ox, pos.y + oy, pos.z + oz,
                    1, 0.0, 0.0, 0.0, 0.08);
        }
    }
}
