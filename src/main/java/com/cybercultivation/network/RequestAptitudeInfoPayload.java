package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestAptitudeInfoPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestAptitudeInfoPayload> STREAM_CODEC =
            StreamCodec.unit(new RequestAptitudeInfoPayload());

    public static final CustomPacketPayload.Type<RequestAptitudeInfoPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "request_aptitude_info")
            );

    @Override
    public CustomPacketPayload.Type<RequestAptitudeInfoPayload> type() {
        return TYPE;
    }
}