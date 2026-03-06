package ghiblicraft.entities.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class SpiritBefriendGoal extends Goal {
    private final PathAwareEntity spirit;
    private PlayerEntity targetPlayer;
    private int followTimer;
    private boolean befriended;
    private static final double FOLLOW_DISTANCE = 4.0;
    private static final double APPROACH_DISTANCE = 2.0;

    public SpiritBefriendGoal(PathAwareEntity spirit) {
        this.spirit = spirit;
        this.befriended = false;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (befriended && targetPlayer != null && targetPlayer.isAlive()) {
            return true;
        }

        // Look for a player holding a sweet berry (offering food)
        PlayerEntity nearest = spirit.getWorld().getClosestPlayer(spirit, 6.0);
        if (nearest != null && isHoldingOffering(nearest)) {
            targetPlayer = nearest;
            return true;
        }
        return befriended && targetPlayer != null;
    }

    private boolean isHoldingOffering(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        return mainHand.isOf(Items.SWEET_BERRIES) || mainHand.isOf(Items.COOKIE)
                || mainHand.isOf(Items.APPLE) || mainHand.isOf(Items.HONEY_BOTTLE);
    }

    @Override
    public void start() {
        if (targetPlayer != null) {
            spirit.getNavigation().startMovingTo(targetPlayer, 0.6);
        }
    }

    @Override
    public void tick() {
        if (targetPlayer == null || !targetPlayer.isAlive()) return;

        spirit.getLookControl().lookAt(targetPlayer, 30.0f, 30.0f);

        if (!befriended) {
            // Approach the player cautiously
            double dist = spirit.distanceTo(targetPlayer);
            if (dist > APPROACH_DISTANCE) {
                spirit.getNavigation().startMovingTo(targetPlayer, 0.4);
            } else if (isHoldingOffering(targetPlayer)) {
                // Accept the offering!
                befriended = true;
                followTimer = 6000; // Follow for 5 minutes

                // Consume one item from the player's hand
                targetPlayer.getMainHandStack().decrement(1);

                // Celebration effects
                if (spirit.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.HEART,
                            spirit.getX(), spirit.getY() + 1, spirit.getZ(),
                            5, 0.3, 0.3, 0.3, 0.1);
                }
                spirit.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

                // Grant a buff to the player based on spirit type
                grantCompanionBuff(targetPlayer);
            }
        } else {
            // Follow behavior
            followTimer--;
            double dist = spirit.distanceTo(targetPlayer);

            if (dist > FOLLOW_DISTANCE) {
                spirit.getNavigation().startMovingTo(targetPlayer, 0.7);
            } else if (dist < 2.0) {
                spirit.getNavigation().stop();
            }

            // Periodically refresh the buff
            if (followTimer % 600 == 0) {
                grantCompanionBuff(targetPlayer);
            }

            // Heart particles occasionally
            if (spirit.getRandom().nextInt(100) == 0 && spirit.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.HEART,
                        spirit.getX(), spirit.getY() + 1, spirit.getZ(),
                        1, 0.2, 0.2, 0.2, 0);
            }

            if (followTimer <= 0) {
                befriended = false;
                targetPlayer = null;
            }
        }
    }

    private void grantCompanionBuff(PlayerEntity player) {
        net.minecraft.entity.effect.StatusEffectInstance effect;
        String spiritClass = spirit.getClass().getSimpleName();

        effect = switch (spiritClass) {
            case "KodamaSpiritEntity" -> new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.LUCK, 1200, 0, true, false);
            case "SootSpriteEntity" -> new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.NIGHT_VISION, 1200, 0, true, false);
            case "CatSpiritEntity" -> new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED, 1200, 0, true, false);
            default -> new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.REGENERATION, 600, 0, true, false);
        };

        player.addStatusEffect(effect);
    }

    @Override
    public boolean shouldContinue() {
        return targetPlayer != null && targetPlayer.isAlive() &&
                (befriended ? followTimer > 0 : spirit.distanceTo(targetPlayer) < 8.0);
    }

    @Override
    public void stop() {
        spirit.getNavigation().stop();
    }

    public boolean isBefriended() {
        return befriended;
    }
}
