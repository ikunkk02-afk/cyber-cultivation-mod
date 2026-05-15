package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Broadcast per-player animation state (meditating / flyingSword) to tracking clients.
 * Sent whenever a player's cultivation state changes so that all nearby clients can
 * drive the correct player animation via Player Animation Library.
 */
public record PlayerAnimationSyncPayload(int entityId, boolean meditating, boolean flyingSword, ResourceLocation flyingSwordItemId)
        implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAnimationSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> {
                        buf.writeVarInt(value.entityId());
                        buf.writeBoolean(value.meditating());
                        buf.writeBoolean(value.flyingSword());
                        writeNullableResourceLocation(buf, value.flyingSwordItemId());
                    },
                    buf -> {
                        int entityId = buf.readVarInt();
                        boolean meditating = buf.readBoolean();
                        boolean flyingSword = buf.readBoolean();
                        ResourceLocation flyingSwordItemId = readNullableResourceLocation(buf);
                        return new PlayerAnimationSyncPayload(entityId, meditating, flyingSword, flyingSwordItemId);
                    }
            );

    public static final CustomPacketPayload.Type<PlayerAnimationSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "player_animation_sync")
            );

    @Override
    public CustomPacketPayload.Type<PlayerAnimationSyncPayload> type() {
        return TYPE;
    }

    private static void writeNullableResourceLocation(RegistryFriendlyByteBuf buf, ResourceLocation value) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeResourceLocation(value);
        }
    }

    private static ResourceLocation readNullableResourceLocation(RegistryFriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        return buf.readResourceLocation();
    }
}
