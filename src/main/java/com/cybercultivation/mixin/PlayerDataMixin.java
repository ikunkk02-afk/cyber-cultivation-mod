package com.cybercultivation.mixin;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.CultivationManualItem;
import com.cybercultivation.item.CultivationManualRank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ServerPlayer.class)
public class PlayerDataMixin {

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onWriteNbt(CompoundTag nbt, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        PlayerQiData data = PlayerQiManager.get(self);
        if (data != null) {
            CompoundTag cultivationNbt = new CompoundTag();
            cultivationNbt.putInt("qi", data.getCurrentQi());
            cultivationNbt.putInt("maxQi", data.getMaxQi());
            cultivationNbt.putBoolean("meditating", data.isMeditating());
            cultivationNbt.putBoolean("aptitudeTested", data.isAptitudeTested());
            putEnum(cultivationNbt, "recommendedPath", data.getRecommendedPath());
            putEnum(cultivationNbt, "recommendedMainDiscipline", data.getRecommendedMainDiscipline());
            putDisciplineList(cultivationNbt, "recommendedSubDisciplines", data.getRecommendedSubDisciplines());
            putEnum(cultivationNbt, "recommendedElement", data.getRecommendedElement());
            putEnum(cultivationNbt, "selectedPath", data.getSelectedPath());
            putEnum(cultivationNbt, "mainDiscipline", data.getMainDiscipline());
            putDisciplineList(cultivationNbt, "subDisciplines", data.getSubDisciplines());
            putEnum(cultivationNbt, "element", data.getElement());
            putManualStudy(cultivationNbt, data);
            putManualRankMap(cultivationNbt, "learnedPathManualRanks", data.getLearnedPathManualRanks());
            putManualRankMap(cultivationNbt, "learnedDisciplineManualRanks", data.getLearnedDisciplineManualRanks());
            cultivationNbt.putBoolean("herbalRealmActive", data.isInHerbalRealm());
            cultivationNbt.putInt("herbalRealmTicksRemaining", data.getHerbalRealmTicksRemaining());
            if (data.hasHerbalRealmReturnPoint()) {
                cultivationNbt.putString("herbalRealmReturnDimension", data.getHerbalRealmReturnDimension().toString());
                cultivationNbt.putDouble("herbalRealmReturnX", data.getHerbalRealmReturnX());
                cultivationNbt.putDouble("herbalRealmReturnY", data.getHerbalRealmReturnY());
                cultivationNbt.putDouble("herbalRealmReturnZ", data.getHerbalRealmReturnZ());
                cultivationNbt.putFloat("herbalRealmReturnYRot", data.getHerbalRealmReturnYRot());
                cultivationNbt.putFloat("herbalRealmReturnXRot", data.getHerbalRealmReturnXRot());
            }
            nbt.put("cyber_cultivation", cultivationNbt);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadNbt(CompoundTag nbt, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        PlayerQiData data = PlayerQiManager.getOrCreate(self);
        if (nbt.contains("cyber_cultivation")) {
            CompoundTag cultivationNbt = nbt.getCompound("cyber_cultivation");
            int qi = cultivationNbt.getInt("qi");
            int maxQi = cultivationNbt.contains("maxQi") ? cultivationNbt.getInt("maxQi") : 100;
            boolean meditating = cultivationNbt.getBoolean("meditating");
            data.setMaxQi(maxQi);
            data.setCurrentQi(qi);
            data.setMeditating(meditating);
            data.setAptitudeTested(cultivationNbt.getBoolean("aptitudeTested"));
            data.setRecommendedPath(readEnum(cultivationNbt, "recommendedPath", CultivationPath.class));
            data.setRecommendedMainDiscipline(readEnum(cultivationNbt, "recommendedMainDiscipline", CultivationDiscipline.class));
            data.setRecommendedSubDisciplines(readDisciplineList(cultivationNbt, "recommendedSubDisciplines"));
            data.setRecommendedElement(readEnum(cultivationNbt, "recommendedElement", CultivationElement.class));
            data.setSelectedPath(readEnum(cultivationNbt, "selectedPath", CultivationPath.class));
            data.setMainDiscipline(readEnum(cultivationNbt, "mainDiscipline", CultivationDiscipline.class));
            data.setSubDisciplines(readDisciplineList(cultivationNbt, "subDisciplines"));
            data.setElement(readEnum(cultivationNbt, "element", CultivationElement.class));
            readManualStudy(cultivationNbt, data);
            readPathManualRanks(cultivationNbt, data);
            readDisciplineManualRanks(cultivationNbt, data);
            boolean herbalRealmActive = cultivationNbt.getBoolean("herbalRealmActive");
            int herbalRealmTicksRemaining = cultivationNbt.contains("herbalRealmTicksRemaining") ? cultivationNbt.getInt("herbalRealmTicksRemaining") : 0;
            net.minecraft.resources.ResourceLocation returnDimension = null;
            if (cultivationNbt.contains("herbalRealmReturnDimension", Tag.TAG_STRING)) {
                returnDimension = net.minecraft.resources.ResourceLocation.tryParse(cultivationNbt.getString("herbalRealmReturnDimension"));
            }
            data.loadHerbalRealmState(
                    herbalRealmActive,
                    herbalRealmTicksRemaining,
                    returnDimension,
                    cultivationNbt.getDouble("herbalRealmReturnX"),
                    cultivationNbt.getDouble("herbalRealmReturnY"),
                    cultivationNbt.getDouble("herbalRealmReturnZ"),
                    cultivationNbt.getFloat("herbalRealmReturnYRot"),
                    cultivationNbt.getFloat("herbalRealmReturnXRot")
            );
        } else {
            // No cultivation NBT — reset to defaults so stale data from a
            // previous world doesn't leak into this one.
            data.reset();
        }
    }

    private static void putEnum(CompoundTag nbt, String key, Enum<?> value) {
        if (value != null) {
            nbt.putString(key, value.name());
        }
    }

    private static <E extends Enum<E>> E readEnum(CompoundTag nbt, String key, Class<E> enumClass) {
        if (!nbt.contains(key, Tag.TAG_STRING)) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, nbt.getString(key));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static void putDisciplineList(CompoundTag nbt, String key, List<CultivationDiscipline> disciplines) {
        ListTag listTag = new ListTag();
        for (CultivationDiscipline discipline : disciplines) {
            listTag.add(StringTag.valueOf(discipline.name()));
        }
        nbt.put(key, listTag);
    }

    private static void putManualStudy(CompoundTag nbt, PlayerQiData data) {
        if (!data.hasManualStudy()) {
            return;
        }
        nbt.putString("studyingManualId", data.getStudyingManualId().toString());
        putEnum(nbt, "studyingManualType", data.getStudyingManualType());
        putEnum(nbt, "studyingManualPath", data.getStudyingManualPath());
        putEnum(nbt, "studyingManualDiscipline", data.getStudyingManualDiscipline());
        putEnum(nbt, "studyingManualRank", data.getStudyingManualRank());
        nbt.putString("studyingManualDisplayName", data.getStudyingManualDisplayName());
        nbt.putInt("studyingManualTicks", data.getStudyingManualTicks());
    }

    private static <E extends Enum<E>> void putManualRankMap(CompoundTag nbt,
                                                             String key,
                                                             Map<E, CultivationManualRank> ranks) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<E, CultivationManualRank> entry : ranks.entrySet()) {
            if (entry.getValue() != null) {
                tag.putString(entry.getKey().name(), entry.getValue().name());
            }
        }
        nbt.put(key, tag);
    }

    private static void readManualStudy(CompoundTag nbt, PlayerQiData data) {
        if (!nbt.contains("studyingManualId", Tag.TAG_STRING)) {
            data.clearManualStudy();
            return;
        }
        ResourceLocation manualId = ResourceLocation.tryParse(nbt.getString("studyingManualId"));
        data.loadManualStudy(
                manualId,
                readEnum(nbt, "studyingManualType", CultivationManualItem.ManualType.class),
                readEnum(nbt, "studyingManualPath", CultivationPath.class),
                readEnum(nbt, "studyingManualDiscipline", CultivationDiscipline.class),
                readEnum(nbt, "studyingManualRank", CultivationManualRank.class),
                nbt.getString("studyingManualDisplayName"),
                nbt.getInt("studyingManualTicks")
        );
    }

    private static void readPathManualRanks(CompoundTag nbt, PlayerQiData data) {
        if (!nbt.contains("learnedPathManualRanks", Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag ranks = nbt.getCompound("learnedPathManualRanks");
        for (String key : ranks.getAllKeys()) {
            CultivationPath path = readEnumValue(key, CultivationPath.class);
            CultivationManualRank rank = readEnumValue(ranks.getString(key), CultivationManualRank.class);
            data.setLearnedPathManualRank(path, rank);
        }
    }

    private static void readDisciplineManualRanks(CompoundTag nbt, PlayerQiData data) {
        if (!nbt.contains("learnedDisciplineManualRanks", Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag ranks = nbt.getCompound("learnedDisciplineManualRanks");
        for (String key : ranks.getAllKeys()) {
            CultivationDiscipline discipline = readEnumValue(key, CultivationDiscipline.class);
            CultivationManualRank rank = readEnumValue(ranks.getString(key), CultivationManualRank.class);
            data.setLearnedDisciplineManualRank(discipline, rank);
        }
    }

    private static <E extends Enum<E>> E readEnumValue(String value, Class<E> enumClass) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static List<CultivationDiscipline> readDisciplineList(CompoundTag nbt, String key) {
        List<CultivationDiscipline> disciplines = new ArrayList<>();
        if (!nbt.contains(key, Tag.TAG_LIST)) {
            return disciplines;
        }
        ListTag listTag = nbt.getList(key, Tag.TAG_STRING);
        for (int i = 0; i < listTag.size(); i++) {
            try {
                disciplines.add(CultivationDiscipline.valueOf(listTag.getString(i)));
            } catch (IllegalArgumentException ignored) {
                // Ignore stale or invalid saved ids from older development builds.
            }
        }
        return disciplines;
    }
}
