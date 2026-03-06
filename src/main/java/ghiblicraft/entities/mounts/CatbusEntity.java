package ghiblicraft.entities.mounts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CatbusEntity extends PathAwareEntity {
    private static final double RUN_SPEED = 1.2;
    private static final double SPRINT_SPEED = 2.0;
    private boolean saddled = false;
    private int happinessTimer = 0;

    public CatbusEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.5f; // Can step up blocks easily
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(1, new LookAroundGoal(this));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);

        if (!saddled && held.isOf(Items.SADDLE)) {
            saddled = true;
            held.decrement(1);
            if (!this.getWorld().isClient) {
                player.sendMessage(Text.literal("The Catbus purrs contentedly!")
                        .formatted(Formatting.YELLOW), true);
                this.playSound(SoundEvents.ENTITY_CAT_PURR, 1.0f, 0.7f);
            }
            return ActionResult.SUCCESS;
        }

        // Feed fish to tame/heal
        if (held.isOf(Items.COD) || held.isOf(Items.SALMON) || held.isOf(Items.TROPICAL_FISH)) {
            this.heal(6.0f);
            held.decrement(1);
            happinessTimer = 600;
            if (this.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + 1.5, this.getZ(),
                        3, 0.3, 0.3, 0.3, 0);
            }
            this.playSound(SoundEvents.ENTITY_CAT_AMBIENT, 1.0f, 0.7f);
            return ActionResult.SUCCESS;
        }

        if (saddled && !this.hasPassengers()) {
            if (!this.getWorld().isClient) {
                player.startRiding(this);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (happinessTimer > 0) happinessTimer--;

        LivingEntity passenger = getControllingPassenger();
        if (passenger != null) {
            this.setYaw(passenger.getYaw());
            this.setPitch(passenger.getPitch() * 0.5f);
            this.setBodyYaw(this.getYaw());
            this.headYaw = this.getYaw();

            float forward = passenger.forwardSpeed;
            float strafe = passenger.sidewaysSpeed;

            double speed = passenger.isSprinting() ? SPRINT_SPEED : RUN_SPEED;
            if (forward < 0) speed *= 0.3; // Slower backward

            float yawRad = this.getYaw() * (float) Math.PI / 180f;
            double mx = (-MathHelper.sin(yawRad) * forward + MathHelper.cos(yawRad) * strafe) * speed;
            double mz = (MathHelper.cos(yawRad) * forward + MathHelper.sin(yawRad) * strafe) * speed;

            // Jump ability
            double my = this.getVelocity().y;
            if (passenger.jumping && this.isOnGround()) {
                my = 0.7; // Big jump
                this.playSound(SoundEvents.ENTITY_CAT_HISS, 0.5f, 0.5f);
            }

            if (!this.isOnGround()) {
                my -= 0.08; // Gravity
            }

            this.setVelocity(new Vec3d(mx * 0.5, my, mz * 0.5).add(this.getVelocity().multiply(0.5)));
            this.move(MovementType.SELF, this.getVelocity());

            // Running particles
            if (this.getWorld().isClient && forward > 0 && this.isOnGround()) {
                for (int i = 0; i < 3; i++) {
                    this.getWorld().addParticle(ParticleTypes.CLOUD,
                            this.getX() + (this.random.nextFloat() - 0.5) * 1.5,
                            this.getY(),
                            this.getZ() + (this.random.nextFloat() - 0.5) * 1.5,
                            0, 0.05, 0);
                }
            }

            // Purring when running
            if (this.age % 40 == 0 && forward > 0) {
                this.playSound(SoundEvents.ENTITY_CAT_PURREOW, 0.3f, 0.6f);
            }
        }

        // Eye glow at night
        if (this.getWorld().isClient && !this.getWorld().isDay()) {
            this.getWorld().addParticle(ParticleTypes.END_ROD,
                    this.getX() + 0.3, this.getY() + 1.6, this.getZ(),
                    0, 0, 0);
            this.getWorld().addParticle(ParticleTypes.END_ROD,
                    this.getX() - 0.3, this.getY() + 1.6, this.getZ(),
                    0, 0, 0);
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public double getMountedHeightOffset() {
        return 1.4;
    }

    @Override
    public boolean canBeControlledByRider() {
        return saddled;
    }

    public boolean isSaddled() {
        return saddled;
    }
}
