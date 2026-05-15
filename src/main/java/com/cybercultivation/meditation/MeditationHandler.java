package com.cybercultivation.meditation;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.particle.SpiritQiParticleOptions;
import com.cybercultivation.util.ParticleColorHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class MeditationHandler {

    private static final int QI_REGEN_PER_SECOND = 2;
    private static final int TICKS_PER_SECOND = 20;
    private static int tickCounter = 0;

    public static void startMeditation(Player player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (data.isMeditating()) {
            player.sendSystemMessage(Component.literal("§e你已经在打坐状态中了。"));
            return;
        }

        data.setMeditating(true);
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                MobEffectInstance.INFINITE_DURATION,
                9,
                true,
                false,
                true
        ));
        player.sendSystemMessage(Component.literal("§a你盘腿坐下，进入打坐状态。"));

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerQiManager.syncToClient(serverPlayer);
            PlayerQiManager.broadcastAnimationState(serverPlayer);
        }
    }

    public static void exitMeditation(Player player) {
        PlayerQiData data = PlayerQiManager.get(player);
        if (data == null || !data.isMeditating()) {
            return;
        }

        data.setMeditating(false);
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.sendSystemMessage(Component.literal("§c你退出了打坐状态。"));

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerQiManager.syncToClient(serverPlayer);
            PlayerQiManager.broadcastAnimationState(serverPlayer);
        }
    }

    public static void toggleMeditation(Player player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (data.isMeditating()) {
            exitMeditation(player);
        } else {
            startMeditation(player);
        }
    }

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < TICKS_PER_SECOND) {
            return;
        }
        tickCounter = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerQiData data = PlayerQiManager.get(player);
            if (data == null || !data.isMeditating()) {
                continue;
            }

            int regenAmount = QI_REGEN_PER_SECOND;
            if (data.getSelectedPath() == CultivationPath.HEAVENLY_DAO) {
                regenAmount += 1;
            }
            data.setCurrentQi(data.getCurrentQi() + regenAmount);

            PlayerQiManager.syncToClient(player);

            spawnMeditationParticles(player);
        }
    }

    private static void spawnMeditationParticles(ServerPlayer player) {
        ServerLevel world = player.serverLevel();
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();

        PlayerQiData data = PlayerQiManager.get(player);
        CultivationPath path = data != null ? data.getSelectedPath() : null;
        int color = ParticleColorHelper.getMeditationPathColor(path);

        for (int i = 0; i < 3; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = 1.0 + world.random.nextDouble() * 0.6;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            double oy = world.random.nextDouble() * 1.5 - 0.2;

            world.sendParticles(
                    new SpiritQiParticleOptions(color),
                    x + ox, y + oy, z + oz,
                    1, 0.0, 0.0, 0.0, 0.01
            );
        }

        if (world.random.nextInt(60) == 0) {
            world.sendParticles(
                    new SpiritQiParticleOptions(color),
                    x + (world.random.nextDouble() - 0.5) * 0.6,
                    y + 0.2,
                    z + (world.random.nextDouble() - 0.5) * 0.6,
                    4, 0.0, 0.4, 0.0, 0.02
            );
        }
    }
}
