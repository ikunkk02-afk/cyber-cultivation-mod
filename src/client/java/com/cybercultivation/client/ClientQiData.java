package com.cybercultivation.client;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    private static ResourceLocation flyingSwordItemId;
    private static boolean meditating;

    public static int getCurrentQi() {
        return currentQi;
    }

    public static int getMaxQi() {
        return maxQi;
    }

    public static String getPathDisplayName() {
        return selectedPath == null ? "\u672a\u9009\u62e9" : selectedPath.getChineseName();
    }

    public static String getMainDisciplineDisplayName() {
        return mainDiscipline == null ? "\u672a\u9009\u62e9" : mainDiscipline.getChineseName();
    }

    public static String getSubDisciplinesDisplayName() {
        return PlayerQiData.formatDisciplines(subDisciplines, "\u65e0");
    }

    public static String getElementDisplayName() {
        return element == null ? "\u672a\u89c9\u9192" : element.getChineseName();
    }

    public static boolean isFlyingSword() {
        return flyingSword;
    }

    public static String getFlyingSwordStatusDisplayName() {
        if (flyingSword) {
            return "\u5fa1\u5251\u4e2d";
        }
        if (meditating) {
            return "\u6253\u5750\u4e2d";
        }
        return "\u6b63\u5e38";
    }

    public static ResourceLocation getFlyingSwordItemId() {
        return flyingSwordItemId;
    }

    public static String getFlyingSwordItemDisplayName() {
        if (!flyingSword || flyingSwordItemId == null) {
            return "";
        }

        Item item = BuiltInRegistries.ITEM.get(flyingSwordItemId);
        if (item == Items.AIR) {
            return "";
        }
        return new ItemStack(item).getHoverName().getString();
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
                           ResourceLocation flyingSwordItemId,
                           boolean meditating) {
        ClientQiData.currentQi = currentQi;
        ClientQiData.maxQi = maxQi;
        ClientQiData.selectedPath = selectedPath;
        ClientQiData.mainDiscipline = mainDiscipline;
        ClientQiData.subDisciplines.clear();
        ClientQiData.subDisciplines.addAll(newSubDisciplines);
        ClientQiData.element = element;
        ClientQiData.flyingSword = flyingSword;
        ClientQiData.flyingSwordItemId = flyingSword ? flyingSwordItemId : null;
        ClientQiData.meditating = meditating;
    }
}
