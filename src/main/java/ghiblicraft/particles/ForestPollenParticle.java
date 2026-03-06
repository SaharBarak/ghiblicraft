package ghiblicraft.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ForestPollenParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected ForestPollenParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.maxAge = 80 + this.random.nextInt(40);
        this.scale = 0.1f + this.random.nextFloat() * 0.05f;
        this.velocityX = velocityX + (this.random.nextFloat() - 0.5) * 0.02;
        this.velocityY = -0.002 + this.random.nextFloat() * 0.004;
        this.velocityZ = velocityZ + (this.random.nextFloat() - 0.5) * 0.02;
        this.collidesWithWorld = false;

        // Warm yellow-green color
        this.red = 0.9f + this.random.nextFloat() * 0.1f;
        this.green = 0.85f + this.random.nextFloat() * 0.1f;
        this.blue = 0.3f + this.random.nextFloat() * 0.2f;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        // Gentle floating drift
        this.velocityX += (this.random.nextFloat() - 0.5) * 0.001;
        this.velocityZ += (this.random.nextFloat() - 0.5) * 0.001;
        this.alpha = 1.0f - ((float) this.age / this.maxAge);
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
            return new ForestPollenParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}
