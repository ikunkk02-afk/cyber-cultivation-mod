package com.cybercultivation.block;

import com.cybercultivation.command.CultivationCommand;
import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.AptitudeGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CultivationAltarBlock extends Block {
    public CultivationAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.15;
        double cz = pos.getZ() + 0.5;

        double ox = (random.nextDouble() - 0.5) * 0.5;
        double oz = (random.nextDouble() - 0.5) * 0.5;
        level.addParticle(
                ParticleTypes.ENCHANT,
                cx + ox, cy + random.nextDouble() * 0.3, cz + oz,
                ox * 0.01, 0.02 + random.nextDouble() * 0.03, oz * 0.01
        );

        if (random.nextInt(3) == 0) {
            level.addParticle(
                    ParticleTypes.END_ROD,
                    cx + (random.nextDouble() - 0.5) * 0.25,
                    cy,
                    cz + (random.nextDouble() - 0.5) * 0.25,
                    0.0, 0.04 + random.nextDouble() * 0.04, 0.0
            );
        }

        if (random.nextInt(5) == 0) {
            level.addParticle(
                    ParticleTypes.SCRAPE,
                    cx + (random.nextDouble() - 0.5) * 0.4,
                    cy + random.nextDouble() * 0.5,
                    cz + (random.nextDouble() - 0.5) * 0.4,
                    (random.nextDouble() - 0.5) * 0.02,
                    0.01,
                    (random.nextDouble() - 0.5) * 0.02
            );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PlayerQiData data = PlayerQiManager.getOrCreate(serverPlayer);
            AptitudeGenerator.generateIfNeeded(serverPlayer.getUUID(), data);
            CultivationCommand.sendAptitudeResult(serverPlayer, data);
            PlayerQiManager.syncToClient(serverPlayer);
            spawnActivationParticles((ServerLevel) level, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void spawnActivationParticles(ServerLevel level, BlockPos pos) {
        RandomSource random = level.random;
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.1;
        double cz = pos.getZ() + 0.5;

        for (int i = 0; i < 24; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.3 + random.nextDouble() * 0.4;
            level.sendParticles(
                    ParticleTypes.END_ROD,
                    cx + Math.cos(angle) * radius,
                    cy + random.nextDouble() * 0.2,
                    cz + Math.sin(angle) * radius,
                    1,
                    Math.cos(angle) * 0.12,
                    0.06 + random.nextDouble() * 0.12,
                    Math.sin(angle) * 0.12,
                    0.01
            );
        }

        for (int i = 0; i < 40; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double height = random.nextDouble() * 1.8;
            double radius = 0.5 + random.nextDouble() * 0.3;
            level.sendParticles(
                    ParticleTypes.ENCHANT,
                    cx + Math.cos(angle) * radius,
                    cy + height,
                    cz + Math.sin(angle) * radius,
                    1,
                    Math.cos(angle) * 0.06,
                    0.03 + random.nextDouble() * 0.06,
                    Math.sin(angle) * 0.06,
                    0.02
            );
        }

        for (int i = 0; i < 15; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 0.08 + random.nextDouble() * 0.12;
            level.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    cx + Math.cos(angle) * 0.25,
                    cy,
                    cz + Math.sin(angle) * 0.25,
                    1,
                    Math.cos(angle) * speed,
                    0.08 + random.nextDouble() * 0.15,
                    Math.sin(angle) * speed,
                    0.01
            );
        }

        for (int i = 0; i < 20; i++) {
            double ox = (random.nextDouble() - 0.5) * 1.2;
            double oz = (random.nextDouble() - 0.5) * 1.2;
            level.sendParticles(
                    ParticleTypes.SCRAPE,
                    cx + ox,
                    cy + 0.6 + random.nextDouble() * 0.6,
                    cz + oz,
                    1,
                    ox * 0.03,
                    0.02 + random.nextDouble() * 0.04,
                    oz * 0.03,
                    0.02
            );
        }
    }
}