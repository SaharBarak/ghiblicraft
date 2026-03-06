package ghiblicraft.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.EnumSet;

public class NoFaceEntity extends PathAwareEntity {
    private PlayerEntity following;
    private int giftCooldown = 0;
    private int emotionState = 0; // 0 = neutral, 1 = happy, 2 = shy

    public NoFaceEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FollowBefriendedPlayerGoal(this));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getStackInHand(hand);

        if (!heldStack.isEmpty() && !this.getWorld().isClient) {
            // No-Face accepts gifts and becomes happy
            this.following = player;
            this.emotionState = 1; // happy

            // Consume the gift
            heldStack.decrement(1);

            // Show gratitude with particles
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + 2, this.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1);
            }

            this.playSound(SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 0.8f, 0.4f);

            // Schedule a return gift
            giftCooldown = 100 + this.random.nextInt(200); // 5-15 seconds

            return ActionResult.SUCCESS;
        }

        // If empty hand, No-Face becomes shy
        if (heldStack.isEmpty() && !this.getWorld().isClient) {
            this.emotionState = 2; // shy
            this.playSound(SoundEvents.ENTITY_ALLAY_HURT, 0.3f, 0.3f);
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (giftCooldown > 0) {
                giftCooldown--;
                if (giftCooldown == 0 && following != null && following.isAlive() &&
                        this.distanceTo(following) < 5.0) {
                    giveReturnGift(following);
                }
            }
        }

        // Visual effects based on emotion
        if (this.getWorld().isClient) {
            switch (emotionState) {
                case 1 -> { // Happy - golden sparkles
                    if (this.random.nextInt(3) == 0) {
                        this.getWorld().addParticle(ParticleTypes.FALLING_NECTAR,
                                this.getX() + random.nextGaussian() * 0.3,
                                this.getY() + 1.5 + random.nextFloat(),
                                this.getZ() + random.nextGaussian() * 0.3,
                                0, 0, 0);
                    }
                }
                case 2 -> { // Shy - subtle smoke
                    if (this.random.nextInt(5) == 0) {
                        this.getWorld().addParticle(ParticleTypes.SMOKE,
                                this.getX(), this.getY() + 1.0, this.getZ(),
                                0, 0.02, 0);
                    }
                }
                default -> { // Neutral - mysterious aura
                    if (this.random.nextInt(8) == 0) {
                        this.getWorld().addParticle(ParticleTypes.PORTAL,
                                this.getX() + random.nextGaussian() * 0.5,
                                this.getY() + 1.0,
                                this.getZ() + random.nextGaussian() * 0.5,
                                0, 0.05, 0);
                    }
                }
            }
        }
    }

    private void giveReturnGift(PlayerEntity player) {
        // No-Face gives mysterious gifts based on what it received
        ItemStack[] possibleGifts = {
                new ItemStack(Items.GOLD_NUGGET, 3 + random.nextInt(5)),
                new ItemStack(Items.EMERALD, 1 + random.nextInt(2)),
                new ItemStack(Items.DIAMOND, 1),
                new ItemStack(Items.AMETHYST_SHARD, 2 + random.nextInt(4)),
                new ItemStack(Items.LAPIS_LAZULI, 4 + random.nextInt(8)),
                new ItemStack(Items.GOLDEN_APPLE, 1),
                new ItemStack(Items.NAME_TAG, 1),
                new ItemStack(Items.MUSIC_DISC_CAT, 1),
        };

        ItemStack gift = possibleGifts[random.nextInt(possibleGifts.length)];
        player.giveItemStack(gift.copy());

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                    this.getX(), this.getY() + 1, this.getZ(),
                    20, 0.5, 0.5, 0.5, 0.5);
        }

        this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        this.emotionState = 0; // Return to neutral
    }

    public int getEmotionState() {
        return emotionState;
    }

    private static class FollowBefriendedPlayerGoal extends Goal {
        private final NoFaceEntity noFace;

        FollowBefriendedPlayerGoal(NoFaceEntity noFace) {
            this.noFace = noFace;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return noFace.following != null && noFace.following.isAlive();
        }

        @Override
        public void tick() {
            if (noFace.following == null) return;
            noFace.getLookControl().lookAt(noFace.following, 30.0f, 30.0f);

            double dist = noFace.distanceTo(noFace.following);
            if (dist > 4.0) {
                noFace.getNavigation().startMovingTo(noFace.following, 0.5);
            } else if (dist < 2.0) {
                noFace.getNavigation().stop();
            }
        }

        @Override
        public boolean shouldContinue() {
            return noFace.following != null && noFace.following.isAlive() &&
                    noFace.distanceTo(noFace.following) < 32.0;
        }

        @Override
        public void stop() {
            noFace.getNavigation().stop();
        }
    }
}
