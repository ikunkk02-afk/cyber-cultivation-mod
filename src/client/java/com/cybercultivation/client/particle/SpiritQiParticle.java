package com.cybercultivation.client.particle;

import com.cybercultivation.particle.SpiritQiParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

public class SpiritQiParticle extends TextureSheetParticle {
    private final double originX;
    private final double originY;
    private final double originZ;
    private final double orbitRadius;
    private final double orbitSpeed;
    private double phase;

    protected SpiritQiParticle(ClientLevel level, double x, double y, double z,
                               double dx, double dy, double dz, SpriteSet sprites, int color) {
        super(level, x, y, z, dx, dy, dz);
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.orbitRadius = 0.5 + level.random.nextDouble() * 0.8;
        this.orbitSpeed = 0.5 + level.random.nextDouble() * 1.5;
        this.phase = level.random.nextDouble() * Math.PI * 2;
        this.lifetime = 50 + level.random.nextInt(31);
        this.gravity = 0;
        this.friction = 0.97F;
        this.quadSize = 0.12F + level.random.nextFloat() * 0.08F;
        this.hasPhysics = false;
        this.setSprite(sprites.get(level.random));
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        setColor(r, g, b);
        setAlpha(a);
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age >= this.lifetime) {
            remove();
            return;
        }
        phase += orbitSpeed * 0.04;
        this.x = originX + Mth.cos((float) phase) * orbitRadius;
        this.z = originZ + Mth.sin((float) phase) * orbitRadius;
        this.yd += 0.003;
        float progress = (float) age / lifetime;
        setAlpha(1.0F - progress * progress);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SpiritQiParticleOptions> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SpiritQiParticleOptions options, ClientLevel level,
                                        double x, double y, double z,
                                        double dx, double dy, double dz) {
            return new SpiritQiParticle(level, x, y, z, dx, dy, dz, sprites, options.color());
        }
    }
}
