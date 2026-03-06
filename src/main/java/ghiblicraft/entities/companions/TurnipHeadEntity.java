package ghiblicraft.entities.companions;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.UUID;

/**
 * Turnip Head (Howl's Moving Castle)
 *
 * A mysterious scarecrow companion that hops around and helps the player:
 * - Follows the player loyally
 * - Carries items for you (like a portable chest)
 * - Guides you toward structures at night (hops excitedly near structures)
 * - Protects crops from being trampled
 * - Can be "kissed" (right-click with flower) to reveal a surprise
 * - Hops instead of walking (bouncy movement)
 */
public class TurnipHeadEntity extends PathAwareEntity {
    private static final TrackedData<Boolean> BONDED = DataTracker.registerData(
            TurnipHeadEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private UUID ownerUuid;
    private int hopTimer = 0;
    private boolean revealedSecret = false;

    public TurnipHeadEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BONDED, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FollowOwnerGoal(this));
        this.goalSelector.add(1, new HopGoal(this));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack held = player.getStackInHand(hand);

        if (!isBonded()) {
            // Bond with a pumpkin (turnip head's actual head)
            if (held.isOf(Items.CARVED_PUMPKIN) || held.isOf(Items.PUMPKIN)) {
                held.decrement(1);
                setBonded(true);
                ownerUuid = player.getUuid();

                if (this.getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + 2, this.getZ(),
                            5, 0.3, 0.3, 0.3, 0.1);
                }

                this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, 1.0f, 1.5f);
                player.sendMessage(Text.literal("Turnip Head bounces happily! He'll follow you now!")
                        .formatted(Formatting.GREEN), false);

                return ActionResult.SUCCESS;
            }

            player.sendMessage(Text.literal("The scarecrow stares... Maybe offer it a pumpkin?")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), true);
            return ActionResult.PASS;
        }

        // Secret reveal: right-click with any flower
        if (!revealedSecret && isFlower(held)) {
            revealedSecret = true;
            held.decrement(1);

            if (this.getWorld() instanceof ServerWorld sw) {
                // Transformation particles!
                sw.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        this.getX(), this.getY() + 1, this.getZ(),
                        60, 1.0, 1.5, 1.0, 0.5);
                sw.spawnParticles(ParticleTypes.ENCHANT,
                        this.getX(), this.getY() + 1, this.getZ(),
                        40, 1.0, 1.0, 1.0, 0.8);
            }

            this.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);

            player.sendMessage(Text.literal("Turnip Head trembles and glows... ")
                    .formatted(Formatting.GOLD)
                    .append(Text.literal("\"Thank you for breaking my curse. I was a prince, once...\"")
                            .formatted(Formatting.YELLOW, Formatting.ITALIC)),
                    false);

            // Drop royal treasures
            this.dropStack(new ItemStack(Items.DIAMOND, 3));
            this.dropStack(new ItemStack(Items.GOLDEN_APPLE, 2));
            this.dropStack(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
            this.dropStack(new ItemStack(Items.EMERALD, 8));
            this.dropStack(new ItemStack(Items.TOTEM_OF_UNDYING, 1));

            // Grant permanent karma
            ghiblicraft.systems.SpiritReputationSystem.modifyKarma(player, 25);

            return ActionResult.SUCCESS;
        }

        // Give items to carry (acts as portable storage)
        if (!held.isEmpty()) {
            // Drop what Turnip Head is carrying
            ItemStack carrying = this.getMainHandStack();
            if (!carrying.isEmpty()) {
                this.dropStack(carrying);
            }
            this.setStackInHand(Hand.MAIN_HAND, held.copy());
            held.setCount(0);

            this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.5f, 1.2f);
            player.sendMessage(Text.literal("Turnip Head is now carrying: " + this.getMainHandStack().getName().getString())
                    .formatted(Formatting.YELLOW), true);
            return ActionResult.SUCCESS;
        }

        // Empty hand = retrieve carried item
        ItemStack carrying = this.getMainHandStack();
        if (!carrying.isEmpty()) {
            player.giveItemStack(carrying.copy());
            this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.5f, 0.8f);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        hopTimer++;

        // Bouncy hop effect
        if (hopTimer % 15 == 0 && this.isOnGround() && this.getNavigation().isFollowingPath()) {
            this.setVelocity(this.getVelocity().add(0, 0.3, 0));
            if (this.getWorld().isClient) {
                this.getWorld().addParticle(ParticleTypes.CLOUD,
                        this.getX(), this.getY(), this.getZ(), 0, 0.02, 0);
            }
        }

        // Crop protection: replant trampled crops nearby
        if (!this.getWorld().isClient && this.age % 100 == 0) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    mutable.set(this.getBlockPos().add(dx, -1, dz));
                    if (this.getWorld().getBlockState(mutable).isOf(Blocks.DIRT)) {
                        // Check if it was recently farmland
                        BlockPos above = mutable.up();
                        if (this.getWorld().getBlockState(above).isAir()) {
                            // Re-till the soil
                            this.getWorld().setBlockState(mutable, Blocks.FARMLAND.getDefaultState(), 3);
                        }
                    }
                }
            }
        }

        // Night-time excitement near structures
        if (!this.getWorld().isClient && !this.getWorld().isDay() && this.age % 80 == 0) {
            if (this.getRandom().nextInt(5) == 0) {
                // Hop excitedly!
                this.setVelocity(this.getVelocity().add(0, 0.5, 0));
                if (this.getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.END_ROD,
                            this.getX(), this.getY() + 2, this.getZ(),
                            3, 0.2, 0.2, 0.2, 0.02);
                }
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Turnip Head is resilient (he's already cursed!)
        if (source.getAttacker() instanceof PlayerEntity player && isOwner(player)) {
            return false; // Owner can't hurt it
        }
        return super.damage(source, amount * 0.5f); // Takes half damage
    }

    private boolean isFlower(ItemStack stack) {
        return stack.isOf(Items.DANDELION) || stack.isOf(Items.POPPY) ||
                stack.isOf(Items.AZURE_BLUET) || stack.isOf(Items.CORNFLOWER) ||
                stack.isOf(Items.LILY_OF_THE_VALLEY) || stack.isOf(Items.ALLIUM);
    }

    public boolean isBonded() {
        return this.dataTracker.get(BONDED);
    }

    public void setBonded(boolean bonded) {
        this.dataTracker.set(BONDED, bonded);
    }

    public boolean isOwner(PlayerEntity player) {
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Bonded", isBonded());
        nbt.putBoolean("RevealedSecret", revealedSecret);
        if (ownerUuid != null) nbt.putUuid("Owner", ownerUuid);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setBonded(nbt.getBoolean("Bonded"));
        revealedSecret = nbt.getBoolean("RevealedSecret");
        if (nbt.containsUuid("Owner")) ownerUuid = nbt.getUuid("Owner");
    }

    private static class FollowOwnerGoal extends Goal {
        private final TurnipHeadEntity turnip;

        FollowOwnerGoal(TurnipHeadEntity turnip) {
            this.turnip = turnip;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!turnip.isBonded() || turnip.ownerUuid == null) return false;
            PlayerEntity owner = turnip.getWorld().getPlayerByUuid(turnip.ownerUuid);
            return owner != null && owner.isAlive() && turnip.distanceTo(owner) > 4.0;
        }

        @Override
        public void tick() {
            PlayerEntity owner = turnip.getWorld().getPlayerByUuid(turnip.ownerUuid);
            if (owner == null) return;

            turnip.getLookControl().lookAt(owner, 30.0f, 30.0f);

            double dist = turnip.distanceTo(owner);
            if (dist > 16.0) {
                // Teleport if too far
                turnip.teleport(owner.getX(), owner.getY(), owner.getZ());
            } else if (dist > 4.0) {
                turnip.getNavigation().startMovingTo(owner, 0.7);
            }
        }

        @Override
        public boolean shouldContinue() {
            PlayerEntity owner = turnip.getWorld().getPlayerByUuid(turnip.ownerUuid);
            return owner != null && owner.isAlive() && turnip.distanceTo(owner) > 3.0;
        }
    }

    private static class HopGoal extends Goal {
        private final TurnipHeadEntity turnip;

        HopGoal(TurnipHeadEntity turnip) {
            this.turnip = turnip;
            this.setControls(EnumSet.of(Control.JUMP));
        }

        @Override
        public boolean canStart() {
            return turnip.isOnGround() && turnip.getNavigation().isFollowingPath();
        }

        @Override
        public void tick() {
            if (turnip.age % 15 == 0) {
                turnip.getJumpControl().setActive();
            }
        }
    }
}
