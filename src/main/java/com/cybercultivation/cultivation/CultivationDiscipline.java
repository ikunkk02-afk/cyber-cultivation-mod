package com.cybercultivation.cultivation;

import java.util.Locale;

public enum CultivationDiscipline {
    SWORD("sword", "剑修", "Sword Cultivation"),
    BODY("body", "体修", "Body Cultivation"),
    ALCHEMY("alchemy", "丹修", "Alchemy"),
    TALISMAN("talisman", "符修", "Talisman"),
    FORMATION("formation", "阵修", "Formation"),
    MEDICAL("medical", "医修", "Medical");

    private final String commandId;
    private final String chineseName;
    private final String englishName;

    CultivationDiscipline(String commandId, String chineseName, String englishName) {
        this.commandId = commandId;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    public String getCommandId() {
        return commandId;
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

    public static CultivationDiscipline fromCommandId(String commandId) {
        String normalized = commandId.toLowerCase(Locale.ROOT);
        for (CultivationDiscipline discipline : values()) {
            if (discipline.commandId.equals(normalized)) {
                return discipline;
            }
        }
        return null;
    }

    public static CultivationDiscipline fromName(String name) {
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
