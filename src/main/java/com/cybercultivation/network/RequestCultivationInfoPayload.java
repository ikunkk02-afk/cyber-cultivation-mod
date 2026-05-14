package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestCultivationInfoPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestCultivationInfoPayload> STREAM_CODEC =
            StreamCodec.unit(new RequestCultivationInfoPayload());

    public static final CustomPacketPayload.Type<RequestCultivationInfoPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "request_cultivation_info")
            );

    @Override
    public CustomPacketPayload.Type<RequestCultivationInfoPayload> type() {
        return TYPE;
    }
}