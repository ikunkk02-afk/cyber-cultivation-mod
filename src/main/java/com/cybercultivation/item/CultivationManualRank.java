package com.cybercultivation.item;

public enum CultivationManualRank {
    BASIC("basic", "初级", "Basic", 1),
    INTERMEDIATE("intermediate", "中级", "Intermediate", 2),
    ADVANCED("advanced", "高级", "Advanced", 3);

    private final String id;
    private final String chineseName;
    private final String englishName;
    private final int level;

    CultivationManualRank(String id, String chineseName, String englishName, int level) {
        this.id = id;
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(CultivationManualRank other) {
        return other == null || level > other.level;
    }
}
