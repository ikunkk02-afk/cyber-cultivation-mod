package com.cybercultivation.network;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.flysword.FlyingSwordHandler;
import com.cybercultivation.meditation.MeditationHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CultivationActions {

    private CultivationActions() {
    }

    public static void toggleMeditation(ServerPlayer player) {
        MeditationHandler.toggleMeditation(player);
    }

    public static void toggleFlyingSword(ServerPlayer player) {
        FlyingSwordHandler.toggle(player, false);
    }

    public static void sendCultivationInfo(ServerPlayer player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);

        String status;
        if (data.isFlyingSword()) {
            status = "§b御剑中";
        } else if (data.isMeditating()) {
            status = "§e打坐中";
        } else {
            status = "§a正常";
        }

        player.sendSystemMessage(Component.literal("§6========== 修仙信息 =========="));
        player.sendSystemMessage(Component.literal(
                String.format("§b灵力: §e%d§7/§e%d", data.getCurrentQi(), data.getMaxQi())
        ));
        player.sendSystemMessage(Component.literal(
                "§b路线: §e" + data.getSelectedPathDisplayName()
        ));
        player.sendSystemMessage(Component.literal(
                "§b主科: §e" + data.getMainDisciplineDisplayName()
        ));
        player.sendSystemMessage(Component.literal(
                "§b副科: §e" + data.formatSubDisciplines()
        ));
        player.sendSystemMessage(Component.literal(
                "§b五行: §e" + data.getElementDisplayName()
        ));
        player.sendSystemMessage(Component.literal(
                "§b状态: " + status
        ));
        player.sendSystemMessage(Component.literal("§6=============================="));

        PlayerQiManager.syncToClient(player);
    }

    public static void sendAptitudeInfo(ServerPlayer player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (!data.isAptitudeTested()) {
            player.sendSystemMessage(Component.literal("§e你尚未检测修仙资质，请先寻找修仙祭坛。"));
            return;
        }
        player.sendSystemMessage(Component.literal("§a你的修仙资质已经显现。"));
        player.sendSystemMessage(Component.literal("§b推荐路线：§e" + data.getRecommendedPathDisplayName()));
        player.sendSystemMessage(Component.literal("§b推荐主科：§e" + data.getRecommendedMainDisciplineDisplayName()));
        player.sendSystemMessage(Component.literal("§b推荐副科：§e" + data.formatRecommendedSubDisciplines()));
        player.sendSystemMessage(Component.literal("§b推荐五行：§e" + data.getRecommendedElementDisplayName()));
        PlayerQiManager.syncToClient(player);
    }
}