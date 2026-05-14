package com.cybercultivation.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleFlyingSwordPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleFlyingSwordPayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleFlyingSwordPayload());

    public static final CustomPacketPayload.Type<ToggleFlyingSwordPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath("cyber-cultivation-mod", "toggle_flying_sword")
            );

    @Override
    public CustomPacketPayload.Type<ToggleFlyingSwordPayload> type() {
        return TYPE;
    }
}