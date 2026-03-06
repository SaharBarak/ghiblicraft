package ghiblicraft.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class FireflyParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private float glowPhase;

    protected FireflyParticle(ClientWorld world, double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ,
                               SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.maxAge = 120 + this.random.nextInt(80);
        this.scale = 0.08f + this.random.nextFloat() * 0.04f;
        this.glowPhase = this.random.nextFloat() * (float) Math.PI * 2;
        this.collidesWithWorld = false;

        // Warm yellow-green glow
        this.red = 0.8f;
        this.green = 1.0f;
        this.blue = 0.2f;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        glowPhase += 0.1f;

        // Pulsating glow
        float glow = (float) (Math.sin(glowPhase) * 0.5 + 0.5);
        this.alpha = 0.3f + glow * 0.7f;

        // Wandering movement
        this.velocityX += (this.random.nextFloat() - 0.5) * 0.005;
        this.velocityY += (this.random.nextFloat() - 0.5) * 0.003;
        this.velocityZ += (this.random.nextFloat() - 0.5) * 0.005;

        // Dampen velocity
        this.velocityX *= 0.95;
        this.velocityY *= 0.95;
        this.velocityZ *= 0.95;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        return 240; // Self-illuminating
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
            return new FireflyParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}
