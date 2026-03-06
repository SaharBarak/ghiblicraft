package ghiblicraft.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class MorningMistParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected MorningMistParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.maxAge = 160 + this.random.nextInt(80);
        this.scale = 1.0f + this.random.nextFloat() * 2.0f;
        this.velocityX = (this.random.nextFloat() - 0.5) * 0.01;
        this.velocityY = 0.001;
        this.velocityZ = (this.random.nextFloat() - 0.5) * 0.01;
        this.collidesWithWorld = false;

        // Soft white-blue mist
        this.red = 0.9f;
        this.green = 0.92f;
        this.blue = 0.95f;
        this.alpha = 0.0f;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        // Fade in, hold, fade out
        float lifeRatio = (float) this.age / this.maxAge;
        if (lifeRatio < 0.2f) {
            this.alpha = lifeRatio * 2.5f; // fade in
        } else if (lifeRatio > 0.7f) {
            this.alpha = (1.0f - lifeRatio) * 3.33f; // fade out
        } else {
            this.alpha = 0.5f;
        }

        // Gentle drift
        this.velocityX += (this.random.nextFloat() - 0.5) * 0.0005;
        this.velocityZ += (this.random.nextFloat() - 0.5) * 0.0005;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new MorningMistParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}
