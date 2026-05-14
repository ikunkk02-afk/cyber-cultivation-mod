package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleMeditationPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleMeditationPayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleMeditationPayload());

    public static final CustomPacketPayload.Type<ToggleMeditationPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "toggle_meditation")
            );

    @Override
    public CustomPacketPayload.Type<ToggleMeditationPayload> type() {
        return TYPE;
    }
}