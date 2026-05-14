package com.cybercultivation.mixin;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

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
