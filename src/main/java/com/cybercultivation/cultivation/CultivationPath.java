package com.cybercultivation.cultivation;

import java.util.Locale;

public enum CultivationPath {
    HEAVENLY_DAO("heavenly", "天道", "Heavenly Dao"),
    HUMAN_DAO("human", "人道", "Human Dao"),
    DEMON_DAO("demon", "魔道", "Demon Dao");

    private final String commandId;
    private final String chineseName;
    private final String englishName;

    CultivationPath(String commandId, String chineseName, String englishName) {
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

    public static CultivationPath fromCommandId(String commandId) {
        String normalized = commandId.toLowerCase(Locale.ROOT);
        for (CultivationPath path : values()) {
            if (path.commandId.equals(normalized)) {
                return path;
            }
        }
        return null;
    }

    public static CultivationPath fromName(String name) {
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
