package ghiblicraft.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class ForestGuardianEntity extends PathAwareEntity {
    private int healCooldown = 0;

    public ForestGuardianEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            healCooldown--;
            if (healCooldown <= 0) {
                healNearbyAnimals();
                healCooldown = 100; // every 5 seconds
            }
        }

        if (this.getWorld().isClient) {
            // Gentle leaf particles around the guardian
            if (this.random.nextInt(5) == 0) {
                this.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getX() + this.random.nextGaussian() * 1.5,
                        this.getY() + this.random.nextFloat() * 3.5,
                        this.getZ() + this.random.nextGaussian() * 1.5,
                        0, 0, 0);
            }
        }
    }

    private void healNearbyAnimals() {
        List<LivingEntity> nearbyEntities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(this.getBlockPos()).expand(12),
                entity -> entity instanceof AnimalEntity && entity.getHealth() < entity.getMaxHealth());

        for (LivingEntity entity : nearbyEntities) {
            entity.heal(2.0f);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
        }
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
