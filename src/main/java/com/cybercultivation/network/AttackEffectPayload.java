package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AttackEffectPayload(int attackerEntityId, ResourceLocation swordItemId)
        implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, AttackEffectPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeVarInt(value.attackerEntityId());
                        buf.writeBoolean(value.swordItemId() != null);
                        if (value.swordItemId() != null) {
                            buf.writeResourceLocation(value.swordItemId());
                        }
                    },
                    buf -> {
                        int id = buf.readVarInt();
                        ResourceLocation sword = buf.readBoolean() ? buf.readResourceLocation() : null;
                        return new AttackEffectPayload(id, sword);
                    }
            );

    public static final CustomPacketPayload.Type<AttackEffectPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "attack_effect")
            );

    @Override
    public CustomPacketPayload.Type<AttackEffectPayload> type() {
        return TYPE;
    }
}
