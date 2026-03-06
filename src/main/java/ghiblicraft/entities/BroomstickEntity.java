package ghiblicraft.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BroomstickEntity extends Entity {
    private static final double FLIGHT_SPEED = 0.8;
    private static final double VERTICAL_SPEED = 0.4;
    private static final double MAX_SPEED = 1.5;
    private int emptyTicks = 0;

    public BroomstickEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = false;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity passenger = getControllingPassenger();
        if (passenger == null) {
            emptyTicks++;
            // Gently descend and despawn when empty
            this.setVelocity(this.getVelocity().multiply(0.9).add(0, -0.04, 0));
            this.move(MovementType.SELF, this.getVelocity());
            if (emptyTicks > 100 || this.isOnGround()) {
                this.discard();
            }
            return;
        }

        emptyTicks = 0;
        this.setYaw(passenger.getYaw());
        this.setPitch(passenger.getPitch() * 0.5f);

        float yawRad = passenger.getYaw() * ((float) Math.PI / 180f);
        float pitchRad = passenger.getPitch() * ((float) Math.PI / 180f);

        double forward = passenger.forwardSpeed;
        double strafe = passenger.sidewaysSpeed;

        // Calculate movement direction
        double motionX = (-MathHelper.sin(yawRad) * forward + MathHelper.cos(yawRad) * strafe) * FLIGHT_SPEED;
        double motionZ = (MathHelper.cos(yawRad) * forward + MathHelper.sin(yawRad) * strafe) * FLIGHT_SPEED;

        // Vertical control based on pitch
        double motionY = 0;
        if (forward > 0) {
            motionY = -MathHelper.sin(pitchRad) * forward * VERTICAL_SPEED;
        }

        // Jump key = ascend, sneak key check is handled by dismounting
        if (passenger.jumping) {
            motionY += VERTICAL_SPEED;
        }

        Vec3d newVelocity = new Vec3d(motionX, motionY, motionZ);

        // Clamp speed
        if (newVelocity.length() > MAX_SPEED) {
            newVelocity = newVelocity.normalize().multiply(MAX_SPEED);
        }

        // Apply momentum
        this.setVelocity(this.getVelocity().multiply(0.3).add(newVelocity.multiply(0.7)));
        this.move(MovementType.SELF, this.getVelocity());

        // Trail particles
        if (this.getWorld().isClient && this.getVelocity().length() > 0.1) {
            for (int i = 0; i < 3; i++) {
                this.getWorld().addParticle(ParticleTypes.END_ROD,
                        this.getX() + (this.random.nextFloat() - 0.5) * 0.5,
                        this.getY(),
                        this.getZ() + (this.random.nextFloat() - 0.5) * 0.5,
                        0, -0.05, 0);
            }
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public double getMountedHeightOffset() {
        return -0.1;
    }
}
