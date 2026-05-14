package com.cybercultivation.network;

import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationElement;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record QiSyncPayload(int currentQi,
                            int maxQi,
                            CultivationPath selectedPath,
                            CultivationDiscipline mainDiscipline,
                            List<CultivationDiscipline> subDisciplines,
                            CultivationElement element,
                            boolean flyingSword,
                            boolean meditating) implements CustomPacketPayload {

    public QiSyncPayload {
        subDisciplines = List.copyOf(subDisciplines);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, QiSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeInt(value.currentQi());
                        buf.writeInt(value.maxQi());
                        writeNullableEnum(buf, value.selectedPath());
                        writeNullableEnum(buf, value.mainDiscipline());
                        buf.writeVarInt(value.subDisciplines().size());
                        for (CultivationDiscipline discipline : value.subDisciplines()) {
                            buf.writeEnum(discipline);
                        }
                        writeNullableEnum(buf, value.element());
                        buf.writeBoolean(value.flyingSword());
                        buf.writeBoolean(value.meditating());
                    },
                    buf -> {
                        int currentQi = buf.readInt();
                        int maxQi = buf.readInt();
                        CultivationPath selectedPath = readNullableEnum(buf, CultivationPath.class);
                        CultivationDiscipline mainDiscipline = readNullableEnum(buf, CultivationDiscipline.class);
                        int subCount = buf.readVarInt();
                        List<CultivationDiscipline> subDisciplines = new ArrayList<>();
                        for (int i = 0; i < subCount; i++) {
                            subDisciplines.add(buf.readEnum(CultivationDiscipline.class));
                        }
                        CultivationElement element = readNullableEnum(buf, CultivationElement.class);
                        boolean flyingSword = buf.readBoolean();
                        boolean meditating = buf.readBoolean();
                        return new QiSyncPayload(currentQi, maxQi, selectedPath, mainDiscipline, subDisciplines, element, flyingSword, meditating);
                    }
            );

    public static final CustomPacketPayload.Type<QiSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "qi_sync")
            );

    @Override
    public CustomPacketPayload.Type<QiSyncPayload> type() {
        return TYPE;
    }

    private static <E extends Enum<E>> void writeNullableEnum(RegistryFriendlyByteBuf buf, E value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeEnum(value);
        }
    }

    private static <E extends Enum<E>> E readNullableEnum(RegistryFriendlyByteBuf buf, Class<E> enumClass) {
        if (!buf.readBoolean()) {
            return null;
        }
        return buf.readEnum(enumClass);
    }
}