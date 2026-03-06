package ghiblicraft.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class SootSpriteEntity extends PathAwareEntity {
    public SootSpriteEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new AvoidSunlightGoal(this));
        this.goalSelector.add(1, new StealItemGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
    }

    @Override
    public void tick() {
        super.tick();

        // Avoid sunlight - take damage in bright light
        if (!this.getWorld().isClient) {
            int skyLight = this.getWorld().getLightLevel(LightType.SKY, this.getBlockPos());
            if (skyLight > 11 && this.getWorld().isDay()) {
                this.damage(this.getDamageSources().onFire(), 1.0f);
            }
        }

        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.SMOKE,
                    this.getX() + this.random.nextGaussian() * 0.1,
                    this.getY() + 0.2,
                    this.getZ() + this.random.nextGaussian() * 0.1,
                    0, 0, 0);
        }
    }

    private static class AvoidSunlightGoal extends Goal {
        private final SootSpriteEntity entity;

        AvoidSunlightGoal(SootSpriteEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            int skyLight = entity.getWorld().getLightLevel(LightType.SKY, entity.getBlockPos());
            return skyLight > 8 && entity.getWorld().isDay();
        }

        @Override
        public void start() {
            entity.getNavigation().startMovingTo(
                    entity.getX() + entity.getRandom().nextGaussian() * 5,
                    entity.getY() - 1,
                    entity.getZ() + entity.getRandom().nextGaussian() * 5,
                    1.2);
        }
    }

    private static class StealItemGoal extends Goal {
        private final SootSpriteEntity entity;
        private ItemEntity targetItem;

        StealItemGoal(SootSpriteEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            List<ItemEntity> items = entity.getWorld().getEntitiesByClass(
                    ItemEntity.class,
                    new Box(entity.getBlockPos()).expand(8),
                    item -> true);
            if (!items.isEmpty()) {
                targetItem = items.get(0);
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (targetItem != null) {
                entity.getNavigation().startMovingTo(targetItem, 1.0);
            }
        }

        @Override
        public void tick() {
            if (targetItem != null && targetItem.isAlive() && entity.distanceTo(targetItem) < 1.5) {
                targetItem.discard();
                entity.playAmbientSound();
            }
        }

        @Override
        public boolean shouldContinue() {
            return targetItem != null && targetItem.isAlive() && entity.distanceTo(targetItem) > 1.5;
        }
    }
}
