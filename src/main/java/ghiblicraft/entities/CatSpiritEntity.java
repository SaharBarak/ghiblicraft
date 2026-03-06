package ghiblicraft.entities;

import ghiblicraft.registry.ModStructures;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CatSpiritEntity extends PathAwareEntity {
    private int guideTimer = 0;
    private BlockPos targetStructure = null;

    public CatSpiritEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            guideTimer++;

            // Occasionally move toward hidden structures when a player is nearby
            if (guideTimer % 600 == 0) { // every 30 seconds
                PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 16.0);
                if (nearestPlayer != null && this.getWorld() instanceof ServerWorld serverWorld) {
                    BlockPos structurePos = serverWorld.locateStructure(
                            serverWorld.getRegistryManager()
                                    .get(net.minecraft.registry.RegistryKeys.STRUCTURE)
                                    .getOrCreateEntryList(net.minecraft.registry.tag.TagKey.of(
                                            net.minecraft.registry.RegistryKeys.STRUCTURE,
                                            new net.minecraft.util.Identifier("ghiblicraft", "cat_spirit_targets"))),
                            this.getBlockPos(), 50, false);

                    if (structurePos != null) {
                        targetStructure = structurePos;
                        this.getNavigation().startMovingTo(
                                structurePos.getX(), structurePos.getY(), structurePos.getZ(), 0.8);
                    }
                }
            }
        }

        // Sparkle trail particles
        if (this.getWorld().isClient && this.random.nextInt(3) == 0) {
            this.getWorld().addParticle(ParticleTypes.ENCHANT,
                    this.getX(), this.getY() + 0.3, this.getZ(),
                    this.random.nextGaussian() * 0.05,
                    this.random.nextFloat() * 0.05,
                    this.random.nextGaussian() * 0.05);
        }
    }

    @Override
    public boolean cannotDespawn() {
        return false;
    }
}
