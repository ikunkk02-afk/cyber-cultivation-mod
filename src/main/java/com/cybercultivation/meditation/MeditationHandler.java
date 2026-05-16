package com.cybercultivation.meditation;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.CultivationManualItem;
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
            advanceManualStudy(player, data);

            PlayerQiManager.syncToClient(player);

            spawnMeditationParticles(player);
        }
    }

    private static void advanceManualStudy(ServerPlayer player, PlayerQiData data) {
        if (!data.hasManualStudy()) {
            return;
        }
        data.advanceManualStudy(TICKS_PER_SECOND);
        if (!data.isManualStudyComplete(CultivationManualItem.STUDY_REQUIRED_TICKS)) {
            return;
        }

        String manualName = data.getStudyingManualDisplayName();
        PlayerQiData.ManualStudyResult preview = previewManualStudyResult(data);
        if (preview != PlayerQiData.ManualStudyResult.LEARNED) {
            data.clearManualStudy();
            sendManualStudyResult(player, preview, manualName);
            return;
        }

        if (!CultivationManualItem.consumeManual(player, data.getStudyingManualId())) {
            data.clearManualStudy();
            player.sendSystemMessage(Component.literal("§c参悟失败：完成时背包中没有原宝典，无法习得内容。"));
            return;
        }

        PlayerQiData.ManualStudyResult result = data.completeManualStudy();
        sendManualStudyResult(player, result, manualName);
    }

    private static PlayerQiData.ManualStudyResult previewManualStudyResult(PlayerQiData data) {
        if (!data.hasManualStudy()) {
            return PlayerQiData.ManualStudyResult.NO_STUDY;
        }
        if (data.getStudyingManualType() == CultivationManualItem.ManualType.PATH
                && data.getSelectedPath() != null
                && data.getSelectedPath() != data.getStudyingManualPath()) {
            return PlayerQiData.ManualStudyResult.CONFLICTING_PATH;
        }
        if (data.getStudyingManualType() == CultivationManualItem.ManualType.DISCIPLINE
                && data.getMainDiscipline() != null
                && data.getMainDiscipline() != data.getStudyingManualDiscipline()
                && !data.getSubDisciplines().contains(data.getStudyingManualDiscipline())
                && data.getSubDisciplines().size() >= PlayerQiData.MAX_SUB_DISCIPLINES) {
            return PlayerQiData.ManualStudyResult.SUB_DISCIPLINES_FULL;
        }
        return PlayerQiData.ManualStudyResult.LEARNED;
    }

    private static void sendManualStudyResult(ServerPlayer player, PlayerQiData.ManualStudyResult result, String manualName) {
        switch (result) {
            case LEARNED -> player.sendSystemMessage(Component.literal("§a你完成参悟，习得了 " + manualName + "。"));
            case CONFLICTING_PATH -> player.sendSystemMessage(Component.literal("§c你已有不同道统，无法改投此宝典道统。"));
            case SUB_DISCIPLINES_FULL -> player.sendSystemMessage(Component.literal("§c你的副业已满，无法再学习新的职业宝典。"));
            case INVALID, NO_STUDY -> player.sendSystemMessage(Component.literal("§c宝典参悟状态异常，本次学习未生效。"));
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
