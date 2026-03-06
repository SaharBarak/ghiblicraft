package ghiblicraft.entities.mounts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

import java.util.UUID;

public class HakuDragonEntity extends PathAwareEntity {
    private static final TrackedData<Boolean> TAMED = DataTracker.registerData(
            HakuDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(
            HakuDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private UUID ownerUuid;
    private int trustLevel = 0; // 0-100, tames at 100
    private static final int TAME_THRESHOLD = 100;
    private static final double FLIGHT_SPEED = 1.0;
    private static final double VERTICAL_SPEED = 0.5;
    private int windParticleTimer = 0;

    public HakuDragonEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = false;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ARMOR, 12.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 1.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMED, false);
        this.dataTracker.startTracking(FLYING, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.3));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);

        if (!isTamed()) {
            // Taming process: feed spirit essence to build trust
            if (held.isOf(Items.LAPIS_LAZULI) || held.isOf(Items.PRISMARINE_SHARD)) {
                trustLevel += 15;
                held.decrement(1);

                if (this.getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.ENCHANT,
                            this.getX(), this.getY() + 2, this.getZ(),
                            10, 0.5, 0.5, 0.5, 0.3);
                }

                if (trustLevel >= TAME_THRESHOLD) {
                    setTamed(true);
                    ownerUuid = player.getUuid();
                    player.sendMessage(Text.literal("Haku remembers your name! The dragon is now bonded to you.")
                            .formatted(Formatting.AQUA, Formatting.BOLD), false);

                    if (this.getWorld() instanceof ServerWorld sw) {
                        sw.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                                this.getX(), this.getY() + 1.5, this.getZ(),
                                50, 1.0, 1.0, 1.0, 0.5);
                    }
                    this.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
                } else {
                    float progress = (float) trustLevel / TAME_THRESHOLD * 100;
                    player.sendMessage(Text.literal(String.format("The dragon eyes you cautiously... (Trust: %.0f%%)", progress))
                            .formatted(Formatting.GRAY, Formatting.ITALIC), true);
                    this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 0.3f, 1.8f);
                }

                return ActionResult.SUCCESS;
            }

            // Gold apple for instant tame
            if (held.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                held.decrement(1);
                setTamed(true);
                ownerUuid = player.getUuid();
                player.sendMessage(Text.literal("The dragon's eyes flash with recognition! A deep bond forms instantly.")
                        .formatted(Formatting.GOLD, Formatting.BOLD), false);
                if (this.getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                            this.getX(), this.getY() + 1.5, this.getZ(),
                            80, 1.5, 1.5, 1.5, 0.8);
                }
                return ActionResult.SUCCESS;
            }

            player.sendMessage(Text.literal("The dragon seems wary. Try offering lapis lazuli or prismarine shards...")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), true);
            return ActionResult.PASS;
        }

        // Tamed behavior
        if (!isOwner(player)) {
            player.sendMessage(Text.literal("This dragon is bonded to another soul.")
                    .formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        // Heal with fish
        if (held.isOf(Items.COD) || held.isOf(Items.SALMON)) {
            this.heal(8.0f);
            held.decrement(1);
            if (this.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + 2, this.getZ(),
                        3, 0.3, 0.3, 0.3, 0);
            }
            return ActionResult.SUCCESS;
        }

        // Mount
        if (!this.hasPassengers()) {
            if (!this.getWorld().isClient) {
                player.startRiding(this);
                setFlying(true);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity passenger = getControllingPassenger();
        if (passenger != null && isTamed()) {
            tickFlightControls(passenger);
        } else if (isFlying() && !this.hasPassengers()) {
            // Gently descend when dismounted
            this.setVelocity(this.getVelocity().multiply(0.9).add(0, -0.05, 0));
            this.move(MovementType.SELF, this.getVelocity());
            if (this.isOnGround()) {
                setFlying(false);
            }
        }

        // Visual effects
        if (this.getWorld().isClient) {
            windParticleTimer++;

            // Mystical aura
            if (this.random.nextInt(5) == 0) {
                this.getWorld().addParticle(ParticleTypes.ENCHANT,
                        this.getX() + random.nextGaussian() * 0.8,
                        this.getY() + 1.0 + random.nextFloat(),
                        this.getZ() + random.nextGaussian() * 0.8,
                        0, 0.1, 0);
            }

            // Flight trail
            if (isFlying() && this.getVelocity().length() > 0.1) {
                for (int i = 0; i < 5; i++) {
                    this.getWorld().addParticle(ParticleTypes.CLOUD,
                            this.getX() + random.nextGaussian() * 0.5,
                            this.getY() + 0.5,
                            this.getZ() + random.nextGaussian() * 0.5,
                            0, -0.02, 0);
                }

                // Water droplets (Haku is a river spirit)
                if (windParticleTimer % 3 == 0) {
                    this.getWorld().addParticle(ParticleTypes.DRIPPING_WATER,
                            this.getX() + random.nextGaussian() * 1.0,
                            this.getY() + 1.5,
                            this.getZ() + random.nextGaussian() * 1.0,
                            0, 0, 0);
                }
            }
        }
    }

    private void tickFlightControls(LivingEntity passenger) {
        this.setYaw(passenger.getYaw());
        this.setPitch(passenger.getPitch() * 0.5f);
        this.setBodyYaw(this.getYaw());
        this.headYaw = this.getYaw();

        float yawRad = passenger.getYaw() * (float) Math.PI / 180f;
        float pitchRad = passenger.getPitch() * (float) Math.PI / 180f;

        double forward = passenger.forwardSpeed;
        double strafe = passenger.sidewaysSpeed;

        double mx = (-MathHelper.sin(yawRad) * forward + MathHelper.cos(yawRad) * strafe) * FLIGHT_SPEED;
        double mz = (MathHelper.cos(yawRad) * forward + MathHelper.sin(yawRad) * strafe) * FLIGHT_SPEED;

        double my = 0;
        if (forward > 0) {
            my = -MathHelper.sin(pitchRad) * forward * VERTICAL_SPEED;
        }

        if (passenger.jumping) {
            my += VERTICAL_SPEED;
        }

        Vec3d newVel = new Vec3d(mx, my, mz);
        if (newVel.length() > 2.0) {
            newVel = newVel.normalize().multiply(2.0);
        }

        this.setVelocity(this.getVelocity().multiply(0.2).add(newVel.multiply(0.8)));
        this.move(MovementType.SELF, this.getVelocity());
        this.noClip = isFlying(); // Phase through blocks slightly while flying
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public double getMountedHeightOffset() {
        return 2.0;
    }

    public boolean isTamed() {
        return this.dataTracker.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.dataTracker.set(TAMED, tamed);
    }

    public boolean isFlying() {
        return this.dataTracker.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.dataTracker.set(FLYING, flying);
    }

    public boolean isOwner(PlayerEntity player) {
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Tamed", isTamed());
        nbt.putInt("Trust", trustLevel);
        if (ownerUuid != null) {
            nbt.putUuid("Owner", ownerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setTamed(nbt.getBoolean("Tamed"));
        trustLevel = nbt.getInt("Trust");
        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner");
        }
    }
}
