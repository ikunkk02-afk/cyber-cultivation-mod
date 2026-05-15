package com.cybercultivation.util;

import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.Map;

public final class ParticleColorHelper {
    private static final Vector3f NORMAL_QI_COLOR = new Vector3f(0.70F, 0.90F, 1.00F);
    private static final Vector3f FLAME_QI_COLOR = new Vector3f(1.00F, 0.40F, 0.10F);
    private static final Vector3f FROST_QI_COLOR = new Vector3f(0.40F, 0.70F, 1.00F);
    private static final Vector3f THUNDER_QI_COLOR = new Vector3f(0.50F, 0.30F, 1.00F);
    private static final Vector3f WIND_QI_COLOR = new Vector3f(0.30F, 0.90F, 0.40F);
    private static final Vector3f STARFALL_QI_COLOR = new Vector3f(0.40F, 0.60F, 1.00F);
    private static final Vector3f HEAVENLY_QI_COLOR = new Vector3f(1.00F, 0.95F, 0.70F);
    private static final Vector3f HUMAN_QI_COLOR = new Vector3f(1.00F, 0.84F, 0.00F);
    private static final Vector3f DEMON_QI_COLOR = new Vector3f(0.60F, 0.00F, 0.10F);
    private static final Vector3f DEFAULT_TRAIL_COLOR = new Vector3f(0.20F, 0.90F, 0.95F);

    private static final Map<Item, Vector3f> SWORD_QI_COLORS = Map.of(
            ModItems.FLAME_SPIRIT_SWORD, FLAME_QI_COLOR,
            ModItems.FROST_SPIRIT_SWORD, FROST_QI_COLOR,
            ModItems.THUNDER_SPIRIT_SWORD, THUNDER_QI_COLOR,
            ModItems.WIND_SPIRIT_SWORD, WIND_QI_COLOR,
            ModItems.STARFALL_SWORD, STARFALL_QI_COLOR,
            ModItems.HEAVENLY_JUDGEMENT_SWORD, HEAVENLY_QI_COLOR,
            ModItems.HUMAN_MERIT_SWORD, HUMAN_QI_COLOR,
            ModItems.DEMON_SOUL_SWORD, DEMON_QI_COLOR
    );

    private static final Map<Item, Vector3f> FLYING_SWORD_TRAIL_COLORS = Map.of(
            ModItems.FLAME_SPIRIT_SWORD, new Vector3f(1.00F, 0.35F, 0.05F),
            ModItems.FROST_SPIRIT_SWORD, new Vector3f(0.35F, 0.65F, 1.00F),
            ModItems.THUNDER_SPIRIT_SWORD, new Vector3f(0.45F, 0.25F, 1.00F),
            ModItems.WIND_SPIRIT_SWORD, new Vector3f(0.25F, 0.85F, 0.35F),
            ModItems.STARFALL_SWORD, new Vector3f(0.50F, 0.70F, 1.00F),
            ModItems.HEAVENLY_JUDGEMENT_SWORD, new Vector3f(1.00F, 0.90F, 0.60F),
            ModItems.HUMAN_MERIT_SWORD, new Vector3f(1.00F, 0.80F, 0.00F),
            ModItems.DEMON_SOUL_SWORD, new Vector3f(0.55F, 0.00F, 0.15F)
    );

    private static final Map<CultivationPath, Integer> MEDITATION_PATH_COLORS = Map.of(
            CultivationPath.HEAVENLY_DAO, 0xCCFFFFFF,
            CultivationPath.HUMAN_DAO, 0xCCFFD700,
            CultivationPath.DEMON_DAO, 0xCC4A0080
    );

    private ParticleColorHelper() {
    }

    public static Vector3f getSwordQiColor(ItemStack stack) {
        if (stack.isEmpty()) return NORMAL_QI_COLOR;
        return SWORD_QI_COLORS.getOrDefault(stack.getItem(), NORMAL_QI_COLOR);
    }

    public static Vector3f getFlyingSwordTrailColor(ItemStack stack) {
        if (stack.isEmpty()) return DEFAULT_TRAIL_COLOR;
        return FLYING_SWORD_TRAIL_COLORS.getOrDefault(stack.getItem(), DEFAULT_TRAIL_COLOR);
    }

    public static int getMeditationPathColor(CultivationPath path) {
        return MEDITATION_PATH_COLORS.getOrDefault(path, 0xCCFFFFFF);
    }

    public static String getSwordAnimationName(ResourceLocation itemId) {
        if (itemId == null) return "normal_swing";
        return switch (itemId.getPath()) {
            case "wind_spirit_sword" -> "wind_slash";
            case "starfall_sword" -> "heavy_slash";
            case "heavenly_judgement_sword" -> "heavenly_strike";
            case "human_merit_sword" -> "human_slash";
            case "demon_soul_sword" -> "demon_slash";
            default -> "normal_swing";
        };
    }

    public static boolean isModSword(ItemStack stack) {
        return !stack.isEmpty() && FlyingSwordHelper.isFlyingSword(stack);
    }
}
