package com.cybercultivation.component;

import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.CultivationManualItem;
import com.cybercultivation.item.CultivationManualRank;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    private boolean herbalRealmActive;
    private int herbalRealmTicksRemaining;
    private ResourceLocation herbalRealmReturnDimension;
    private double herbalRealmReturnX;
    private double herbalRealmReturnY;
    private double herbalRealmReturnZ;
    private float herbalRealmReturnYRot;
    private float herbalRealmReturnXRot;
    private ResourceLocation studyingManualId;
    private CultivationManualItem.ManualType studyingManualType;
    private CultivationPath studyingManualPath;
    private CultivationDiscipline studyingManualDiscipline;
    private CultivationManualRank studyingManualRank;
    private String studyingManualDisplayName;
    private int studyingManualTicks;
    private final Map<CultivationPath, CultivationManualRank> learnedPathManualRanks;
    private final Map<CultivationDiscipline, CultivationManualRank> learnedDisciplineManualRanks;

    public PlayerQiData() {
        this.recommendedSubDisciplines = new ArrayList<>();
        this.subDisciplines = new ArrayList<>();
        this.learnedPathManualRanks = new EnumMap<>(CultivationPath.class);
        this.learnedDisciplineManualRanks = new EnumMap<>(CultivationDiscipline.class);
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
        clearHerbalRealm();
        this.recommendedPath = null;
        this.recommendedMainDiscipline = null;
        this.recommendedElement = null;
        this.selectedPath = null;
        this.mainDiscipline = null;
        this.element = null;
        this.recommendedSubDisciplines.clear();
        this.subDisciplines.clear();
        this.learnedPathManualRanks.clear();
        this.learnedDisciplineManualRanks.clear();
        clearManualStudy();
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

    public boolean isInHerbalRealm() {
        return herbalRealmActive;
    }

    public int getHerbalRealmTicksRemaining() {
        return Math.max(0, herbalRealmTicksRemaining);
    }

    public int decrementHerbalRealmTick() {
        if (herbalRealmActive && herbalRealmTicksRemaining > 0) {
            herbalRealmTicksRemaining--;
        }
        return getHerbalRealmTicksRemaining();
    }

    public void startHerbalRealm(ResourceLocation returnDimension,
                                 double returnX,
                                 double returnY,
                                 double returnZ,
                                 float returnYRot,
                                 float returnXRot,
                                 int ticksRemaining) {
        this.herbalRealmActive = true;
        this.herbalRealmTicksRemaining = Math.max(0, ticksRemaining);
        this.herbalRealmReturnDimension = returnDimension;
        this.herbalRealmReturnX = returnX;
        this.herbalRealmReturnY = returnY;
        this.herbalRealmReturnZ = returnZ;
        this.herbalRealmReturnYRot = returnYRot;
        this.herbalRealmReturnXRot = returnXRot;
    }

    public void loadHerbalRealmState(boolean active,
                                     int ticksRemaining,
                                     ResourceLocation returnDimension,
                                     double returnX,
                                     double returnY,
                                     double returnZ,
                                     float returnYRot,
                                     float returnXRot) {
        this.herbalRealmActive = active;
        this.herbalRealmTicksRemaining = Math.max(0, ticksRemaining);
        this.herbalRealmReturnDimension = returnDimension;
        this.herbalRealmReturnX = returnX;
        this.herbalRealmReturnY = returnY;
        this.herbalRealmReturnZ = returnZ;
        this.herbalRealmReturnYRot = returnYRot;
        this.herbalRealmReturnXRot = returnXRot;
        if (!active) {
            clearHerbalRealm();
        }
    }

    public void clearHerbalRealm() {
        this.herbalRealmActive = false;
        this.herbalRealmTicksRemaining = 0;
        this.herbalRealmReturnDimension = null;
        this.herbalRealmReturnX = 0.0D;
        this.herbalRealmReturnY = 0.0D;
        this.herbalRealmReturnZ = 0.0D;
        this.herbalRealmReturnYRot = 0.0F;
        this.herbalRealmReturnXRot = 0.0F;
    }

    public boolean hasHerbalRealmReturnPoint() {
        return herbalRealmReturnDimension != null;
    }

    public ResourceLocation getHerbalRealmReturnDimension() {
        return herbalRealmReturnDimension;
    }

    public double getHerbalRealmReturnX() {
        return herbalRealmReturnX;
    }

    public double getHerbalRealmReturnY() {
        return herbalRealmReturnY;
    }

    public double getHerbalRealmReturnZ() {
        return herbalRealmReturnZ;
    }

    public float getHerbalRealmReturnYRot() {
        return herbalRealmReturnYRot;
    }

    public float getHerbalRealmReturnXRot() {
        return herbalRealmReturnXRot;
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

    public boolean hasManualStudy() {
        return studyingManualId != null;
    }

    public void startManualStudy(ResourceLocation manualId,
                                 CultivationManualItem.ManualType manualType,
                                 CultivationPath path,
                                 CultivationDiscipline discipline,
                                 CultivationManualRank rank,
                                 String displayName) {
        this.studyingManualId = manualId;
        this.studyingManualType = manualType;
        this.studyingManualPath = path;
        this.studyingManualDiscipline = discipline;
        this.studyingManualRank = rank;
        this.studyingManualDisplayName = displayName;
        this.studyingManualTicks = 0;
    }

    public void loadManualStudy(ResourceLocation manualId,
                                CultivationManualItem.ManualType manualType,
                                CultivationPath path,
                                CultivationDiscipline discipline,
                                CultivationManualRank rank,
                                String displayName,
                                int ticks) {
        this.studyingManualId = manualId;
        this.studyingManualType = manualType;
        this.studyingManualPath = path;
        this.studyingManualDiscipline = discipline;
        this.studyingManualRank = rank;
        this.studyingManualDisplayName = displayName;
        this.studyingManualTicks = Math.max(0, ticks);
        if (manualId == null || manualType == null || rank == null || (path == null && discipline == null)) {
            clearManualStudy();
        }
    }

    public void clearManualStudy() {
        this.studyingManualId = null;
        this.studyingManualType = null;
        this.studyingManualPath = null;
        this.studyingManualDiscipline = null;
        this.studyingManualRank = null;
        this.studyingManualDisplayName = "";
        this.studyingManualTicks = 0;
    }

    public ResourceLocation getStudyingManualId() {
        return studyingManualId;
    }

    public CultivationManualItem.ManualType getStudyingManualType() {
        return studyingManualType;
    }

    public CultivationPath getStudyingManualPath() {
        return studyingManualPath;
    }

    public CultivationDiscipline getStudyingManualDiscipline() {
        return studyingManualDiscipline;
    }

    public CultivationManualRank getStudyingManualRank() {
        return studyingManualRank;
    }

    public String getStudyingManualDisplayName() {
        if (studyingManualDisplayName == null || studyingManualDisplayName.isBlank()) {
            return "未知宝典";
        }
        return studyingManualDisplayName;
    }

    public int getStudyingManualTicks() {
        return studyingManualTicks;
    }

    public int getStudyingManualProgressSeconds() {
        return Math.min(60, studyingManualTicks / 20);
    }

    public int advanceManualStudy(int ticks) {
        if (!hasManualStudy()) {
            return 0;
        }
        studyingManualTicks = Math.max(0, studyingManualTicks + ticks);
        return studyingManualTicks;
    }

    public boolean isManualStudyComplete(int requiredTicks) {
        return hasManualStudy() && studyingManualTicks >= requiredTicks;
    }

    public CultivationManualRank getLearnedPathManualRank(CultivationPath path) {
        return learnedPathManualRanks.get(path);
    }

    public CultivationManualRank getLearnedDisciplineManualRank(CultivationDiscipline discipline) {
        return learnedDisciplineManualRanks.get(discipline);
    }

    public Map<CultivationPath, CultivationManualRank> getLearnedPathManualRanks() {
        return Map.copyOf(learnedPathManualRanks);
    }

    public Map<CultivationDiscipline, CultivationManualRank> getLearnedDisciplineManualRanks() {
        return Map.copyOf(learnedDisciplineManualRanks);
    }

    public void setLearnedPathManualRank(CultivationPath path, CultivationManualRank rank) {
        if (path != null && rank != null) {
            learnedPathManualRanks.put(path, rank);
        }
    }

    public void setLearnedDisciplineManualRank(CultivationDiscipline discipline, CultivationManualRank rank) {
        if (discipline != null && rank != null) {
            learnedDisciplineManualRanks.put(discipline, rank);
        }
    }

    public ManualStudyResult completeManualStudy() {
        if (!hasManualStudy()) {
            return ManualStudyResult.NO_STUDY;
        }
        if (studyingManualType == CultivationManualItem.ManualType.PATH) {
            if (selectedPath != null && selectedPath != studyingManualPath) {
                clearManualStudy();
                return ManualStudyResult.CONFLICTING_PATH;
            }
            selectedPath = studyingManualPath;
            setLearnedPathManualRank(studyingManualPath, highestRank(getLearnedPathManualRank(studyingManualPath), studyingManualRank));
            clearManualStudy();
            return ManualStudyResult.LEARNED;
        }
        if (studyingManualType == CultivationManualItem.ManualType.DISCIPLINE) {
            if (mainDiscipline == null) {
                mainDiscipline = studyingManualDiscipline;
            } else if (mainDiscipline != studyingManualDiscipline && !subDisciplines.contains(studyingManualDiscipline)) {
                if (subDisciplines.size() >= MAX_SUB_DISCIPLINES) {
                    clearManualStudy();
                    return ManualStudyResult.SUB_DISCIPLINES_FULL;
                }
                subDisciplines.add(studyingManualDiscipline);
            }
            setLearnedDisciplineManualRank(studyingManualDiscipline, highestRank(getLearnedDisciplineManualRank(studyingManualDiscipline), studyingManualRank));
            clearManualStudy();
            return ManualStudyResult.LEARNED;
        }
        clearManualStudy();
        return ManualStudyResult.INVALID;
    }

    private static CultivationManualRank highestRank(CultivationManualRank current, CultivationManualRank next) {
        return next != null && next.isHigherThan(current) ? next : current;
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

    public enum ManualStudyResult {
        LEARNED,
        CONFLICTING_PATH,
        SUB_DISCIPLINES_FULL,
        INVALID,
        NO_STUDY
    }
}
