package com.cybercultivation.cultivation;

import com.cybercultivation.component.PlayerQiData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.stream.Collectors;

public final class CultivationDisplay {
    private CultivationDisplay() {
    }

    public static String pathName(CultivationPath path) {
        return path == null ? "未选择" : path.getDisplayName();
    }

    public static String disciplineName(CultivationDiscipline discipline) {
        return discipline == null ? "未选择" : discipline.getDisplayName();
    }

    public static String disciplineList(List<CultivationDiscipline> disciplines) {
        if (disciplines == null || disciplines.isEmpty()) {
            return "无";
        }
        return disciplines.stream()
                .map(CultivationDiscipline::getDisplayName)
                .collect(Collectors.joining("、"));
    }

    public static String elementName(CultivationElement element) {
        return element == null ? "未觉醒" : element.getDisplayName();
    }

    public static void sendAptitudeResult(ServerPlayer player, PlayerQiData data) {
        player.sendSystemMessage(Component.literal("§a你的修仙资质已经显现。"));
        player.sendSystemMessage(Component.literal("§b推荐路线：§e" + pathName(data.getRecommendedPath())));
        player.sendSystemMessage(Component.literal("§b推荐主科：§e" + disciplineName(data.getRecommendedMainDiscipline())));
        player.sendSystemMessage(Component.literal("§b推荐副科：§e" + disciplineList(data.getRecommendedSubDisciplines())));
        player.sendSystemMessage(Component.literal("§b推荐五行：§e" + elementName(data.getRecommendedElement())));
    }
}
