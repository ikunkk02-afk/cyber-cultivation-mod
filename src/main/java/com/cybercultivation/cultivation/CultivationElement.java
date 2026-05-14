package com.cybercultivation.cultivation;

public enum CultivationElement {
    METAL("金", "Metal"),
    WOOD("木", "Wood"),
    WATER("水", "Water"),
    FIRE("火", "Fire"),
    EARTH("土", "Earth");

    private final String chineseName;
    private final String englishName;

    CultivationElement(String chineseName, String englishName) {
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getDisplayName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public static CultivationElement fromName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
