package ghiblicraft.entities;

import ghiblicraft.entities.ai.SpiritBefriendGoal;
import ghiblicraft.registry.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class KodamaSpiritEntity extends PathAwareEntity {
    private int idleTicks = 0;

    public KodamaSpiritEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SpiritBefriendGoal(this));
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            idleTicks++;
            // Bob head animation via particle effects
            if (idleTicks % 40 == 0) {
                this.getWorld().addParticle(ParticleTypes.END_ROD,
                        this.getX(), this.getY() + 0.8, this.getZ(),
                        0, 0.02, 0);
            }
        }

        // Disappear if player runs toward it
        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 3.0);
        if (nearestPlayer != null && nearestPlayer.isSprinting()) {
            if (!this.getWorld().isClient) {
                this.getWorld().sendEntityStatus(this, (byte) 60);
                this.playSound(ModSounds.SPIRIT_WHISPER, 0.5f, 1.5f);
                this.discard();
            }
        }
    }

    @Override
    public boolean cannotDespawn() {
        return false;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }
}
