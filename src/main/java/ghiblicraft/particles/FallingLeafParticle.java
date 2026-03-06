package ghiblicraft.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class FallingLeafParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float swaySpeed;

    protected FallingLeafParticle(ClientWorld world, double x, double y, double z,
                                   double velocityX, double velocityY, double velocityZ,
                                   SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.maxAge = 100 + this.random.nextInt(60);
        this.scale = 0.15f + this.random.nextFloat() * 0.1f;
        this.swaySpeed = 0.03f + this.random.nextFloat() * 0.02f;
        this.velocityY = -0.01 - this.random.nextFloat() * 0.01;
        this.collidesWithWorld = true;

        // Autumn leaf colors
        float colorChoice = this.random.nextFloat();
        if (colorChoice < 0.33f) {
            // Green
            this.red = 0.3f; this.green = 0.7f; this.blue = 0.2f;
        } else if (colorChoice < 0.66f) {
            // Orange
            this.red = 0.9f; this.green = 0.5f; this.blue = 0.1f;
        } else {
            // Red
            this.red = 0.8f; this.green = 0.2f; this.blue = 0.1f;
        }

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        // Swaying motion
        this.velocityX = Math.sin(this.age * swaySpeed) * 0.01;
        this.velocityZ = Math.cos(this.age * swaySpeed * 0.7) * 0.008;
        this.angle += 0.02f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}
