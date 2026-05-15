package com.cybercultivation.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SpiritQiParticleOptions(int color) implements ParticleOptions {
    public static final MapCodec<SpiritQiParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("color").forGetter(SpiritQiParticleOptions::color)
            ).apply(instance, SpiritQiParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SpiritQiParticleOptions> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> buf.writeInt(value.color()),
                    buf -> new SpiritQiParticleOptions(buf.readInt())
            );

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SPIRIT_QI;
    }
}
