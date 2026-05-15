package com.cybercultivation.component;

import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlayerQiData {
    public static final int MAX_SUB_DISCIPLINES = 2;

    private int currentQi;
    private int maxQi;
    private boolean meditating;
    private boolean aptitudeTested;
    private CultivationPath recommendedPath;
    private CultivationDiscipline recommendedMainDiscipline;
    private final List<CultivationDiscipline> recommendedSubDisciplines;
    private CultivationElement recommendedElement;
    private CultivationPath selectedPath;
    private CultivationDiscipline mainDiscipline;
    private final List<CultivationDiscipline> subDisciplines;
    private CultivationElement element;
    private boolean flyingSword;
    private ResourceLocation flyingSwordItemId;

    public PlayerQiData() {
        this.recommendedSubDisciplines = new ArrayList<>();
        this.subDisciplines = new ArrayList<>();
        reset();
    }

    /** Reset all fields to initial defaults. */
    public void reset() {
        this.currentQi = 0;
        this.maxQi = 100;
        this.meditating = false;
        this.aptitudeTested = false;
        this.flyingSword = false;
        this.flyingSwordItemId = null;
        this.recommendedPath = null;
        this.recommendedMainDiscipline = null;
        this.recommendedElement = null;
        this.selectedPath = null;
        this.mainDiscipline = null;
        this.element = null;
        this.recommendedSubDisciplines.clear();
        this.subDisciplines.clear();
    }

    public int getCurrentQi() {
        return currentQi;
    }

    public void setCurrentQi(int currentQi) {
        this.currentQi = Math.max(0, Math.min(currentQi, maxQi));
    }

    public int getMaxQi() {
        return maxQi;
    }

    public void setMaxQi(int maxQi) {
        this.maxQi = Math.max(1, maxQi);
        if (this.currentQi > this.maxQi) {
            this.currentQi = this.maxQi;
        }
    }

    public boolean isMeditating() {
        return meditating;
    }

    public void setMeditating(boolean meditating) {
        this.meditating = meditating;
    }

    public boolean isAptitudeTested() {
        return aptitudeTested;
    }

    public void setAptitudeTested(boolean aptitudeTested) {
        this.aptitudeTested = aptitudeTested;
    }

    public CultivationPath getRecommendedPath() {
        return recommendedPath;
    }

    public void setRecommendedPath(CultivationPath recommendedPath) {
        this.recommendedPath = recommendedPath;
    }

    public CultivationDiscipline getRecommendedMainDiscipline() {
        return recommendedMainDiscipline;
    }

    public void setRecommendedMainDiscipline(CultivationDiscipline recommendedMainDiscipline) {
        this.recommendedMainDiscipline = recommendedMainDiscipline;
    }

    public List<CultivationDiscipline> getRecommendedSubDisciplines() {
        return List.copyOf(recommendedSubDisciplines);
    }

    public void setRecommendedSubDisciplines(List<CultivationDiscipline> disciplines) {
        recommendedSubDisciplines.clear();
        addUniqueDisciplines(recommendedSubDisciplines, disciplines, MAX_SUB_DISCIPLINES, recommendedMainDiscipline);
    }

    public CultivationElement getRecommendedElement() {
        return recommendedElement;
    }

    public void setRecommendedElement(CultivationElement recommendedElement) {
        this.recommendedElement = recommendedElement;
    }

    public CultivationPath getSelectedPath() {
        return selectedPath;
    }

    public void setSelectedPath(CultivationPath selectedPath) {
        this.selectedPath = selectedPath;
    }

    public CultivationDiscipline getMainDiscipline() {
        return mainDiscipline;
    }

    public boolean setMainDiscipline(CultivationDiscipline mainDiscipline) {
        if (mainDiscipline != null && subDisciplines.contains(mainDiscipline)) {
            return false;
        }
        this.mainDiscipline = mainDiscipline;
        return true;
    }

    public List<CultivationDiscipline> getSubDisciplines() {
        return List.copyOf(subDisciplines);
    }

    public void setSubDisciplines(List<CultivationDiscipline> disciplines) {
        subDisciplines.clear();
        addUniqueDisciplines(subDisciplines, disciplines, MAX_SUB_DISCIPLINES, mainDiscipline);
    }

    public boolean addSubDiscipline(CultivationDiscipline discipline) {
        if (discipline == null || discipline == mainDiscipline || subDisciplines.contains(discipline)) {
            return false;
        }
        if (subDisciplines.size() >= MAX_SUB_DISCIPLINES) {
            return false;
        }
        subDisciplines.add(discipline);
        return true;
    }

    public boolean removeSubDiscipline(CultivationDiscipline discipline) {
        return subDisciplines.remove(discipline);
    }

    public CultivationElement getElement() {
        return element;
    }

    public void setElement(CultivationElement element) {
        this.element = element;
    }

    public boolean isFlyingSword() {
        return flyingSword;
    }

    public void setFlyingSword(boolean flyingSword) {
        this.flyingSword = flyingSword;
        if (!flyingSword) {
            this.flyingSwordItemId = null;
        }
    }

    public ResourceLocation getFlyingSwordItemId() {
        return flyingSwordItemId;
    }

    public void setFlyingSwordItemId(ResourceLocation flyingSwordItemId) {
        this.flyingSwordItemId = flyingSwordItemId;
    }

    public void applyAptitudeResult(CultivationPath path,
                                    CultivationDiscipline mainDiscipline,
                                    List<CultivationDiscipline> subDisciplines,
                                    CultivationElement element) {
        this.aptitudeTested = true;
        this.recommendedPath = path;
        this.recommendedMainDiscipline = mainDiscipline;
        this.recommendedElement = element;
        this.element = element;
        setRecommendedSubDisciplines(subDisciplines);
    }

    public String getSelectedPathDisplayName() {
        return selectedPath == null ? "未选择" : selectedPath.getChineseName();
    }

    public String getMainDisciplineDisplayName() {
        return mainDiscipline == null ? "未选择" : mainDiscipline.getChineseName();
    }

    public String getElementDisplayName() {
        return element == null ? "未觉醒" : element.getChineseName();
    }

    public String getRecommendedPathDisplayName() {
        return recommendedPath == null ? "未知" : recommendedPath.getChineseName();
    }

    public String getRecommendedMainDisciplineDisplayName() {
        return recommendedMainDiscipline == null ? "未知" : recommendedMainDiscipline.getChineseName();
    }

    public String getRecommendedElementDisplayName() {
        return recommendedElement == null ? "未知" : recommendedElement.getChineseName();
    }

    public String formatSubDisciplines() {
        return formatDisciplines(subDisciplines, "无");
    }

    public String formatRecommendedSubDisciplines() {
        return formatDisciplines(recommendedSubDisciplines, "未知");
    }

    public static String formatDisciplines(List<CultivationDiscipline> disciplines, String emptyText) {
        if (disciplines.isEmpty()) {
            return emptyText;
        }
        List<String> names = new ArrayList<>();
        for (CultivationDiscipline discipline : disciplines) {
            names.add(discipline.getChineseName());
        }
        return String.join("、", names);
    }

    private static void addUniqueDisciplines(List<CultivationDiscipline> target,
                                             List<CultivationDiscipline> source,
                                             int maxCount,
                                             CultivationDiscipline excluded) {
        for (CultivationDiscipline discipline : source) {
            if (discipline == null || discipline == excluded || target.contains(discipline)) {
                continue;
            }
            target.add(discipline);
            if (target.size() >= maxCount) {
                return;
            }
        }
    }
}
