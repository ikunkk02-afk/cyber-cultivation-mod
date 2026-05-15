package com.cybercultivation.particle;

import com.cybercultivation.CyberCultivationMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ModParticles {
    public static final ParticleType<SpiritQiParticleOptions> SPIRIT_QI = register(
            "spirit_qi",
            new ParticleType<SpiritQiParticleOptions>(false) {
                @Override
                public MapCodec<SpiritQiParticleOptions> codec() {
                    return SpiritQiParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, SpiritQiParticleOptions> streamCodec() {
                    return SpiritQiParticleOptions.STREAM_CODEC;
                }
            }
    );

    private ModParticles() {
    }

    public static void register() {
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation particles");
    }

    private static <T extends ParticleOptions> ParticleType<T> register(String name, ParticleType<T> type) {
        return Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name),
                type
        );
    }
}
