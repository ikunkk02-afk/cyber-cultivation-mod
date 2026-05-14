package com.cybercultivation.client;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;

import java.util.ArrayList;
import java.util.List;

public class ClientQiData {
    private static int currentQi = 0;
    private static int maxQi = 100;
    private static CultivationPath selectedPath;
    private static CultivationDiscipline mainDiscipline;
    private static final List<CultivationDiscipline> subDisciplines = new ArrayList<>();
    private static CultivationElement element;
    private static boolean flyingSword;
    private static boolean meditating;

    public static int getCurrentQi() {
        return currentQi;
    }

    public static int getMaxQi() {
        return maxQi;
    }

    public static String getPathDisplayName() {
        return selectedPath == null ? "未选择" : selectedPath.getChineseName();
    }

    public static String getMainDisciplineDisplayName() {
        return mainDiscipline == null ? "未选择" : mainDiscipline.getChineseName();
    }

    public static String getSubDisciplinesDisplayName() {
        return PlayerQiData.formatDisciplines(subDisciplines, "无");
    }

    public static String getElementDisplayName() {
        return element == null ? "未觉醒" : element.getChineseName();
    }

    public static boolean isFlyingSword() {
        return flyingSword;
    }

    public static String getFlyingSwordStatusDisplayName() {
        if (flyingSword) {
            return "御剑中";
        }
        if (meditating) {
            return "打坐中";
        }
        return "正常";
    }

    public static boolean isMeditating() {
        return meditating;
    }

    public static void set(int currentQi,
                           int maxQi,
                           CultivationPath selectedPath,
                           CultivationDiscipline mainDiscipline,
                           List<CultivationDiscipline> newSubDisciplines,
                           CultivationElement element,
                           boolean flyingSword,
                           boolean meditating) {
        ClientQiData.currentQi = currentQi;
        ClientQiData.maxQi = maxQi;
        ClientQiData.selectedPath = selectedPath;
        ClientQiData.mainDiscipline = mainDiscipline;
        ClientQiData.subDisciplines.clear();
        ClientQiData.subDisciplines.addAll(newSubDisciplines);
        ClientQiData.element = element;
        ClientQiData.flyingSword = flyingSword;
        ClientQiData.meditating = meditating;
    }
}