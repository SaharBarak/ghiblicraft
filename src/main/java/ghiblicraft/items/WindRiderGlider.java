package ghiblicraft.items;

import ghiblicraft.systems.WindCurrentSystem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Wind Rider Glider (Nausicaä of the Valley of the Wind)
 *
 * A glider that lets the player soar on wind currents. When activated while
 * airborne, the player glides forward with slow falling. Wind currents in
 * certain biomes and altitudes provide speed boosts and lift.
 *
 * - Hold right-click while falling to glide
 * - Look direction controls glide direction
 * - Wind currents near mountains/valleys provide boost
 * - Durability decreases while gliding
 */
public class WindRiderGlider extends Item {
    private static final double GLIDE_SPEED = 0.8;
    private static final double LIFT_FACTOR = 0.03;
    private static final double GRAVITY_REDUCTION = 0.06;

    public WindRiderGlider() {
        super(new FabricItemSettings().maxCount(1).maxDamage(800));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!player.isOnGround()) {
            // Activate gliding
            if (!world.isClient) {
                applyGlidePhysics(player, world);
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));

                // Sound
                if (player.age % 20 == 0) {
                    world.playSound(null, player.getBlockPos(),
                            SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, 0.3f, 1.5f);
                }
            }

            // Visual effects
            if (world.isClient) {
                for (int i = 0; i < 2; i++) {
                    world.addParticle(ParticleTypes.CLOUD,
                            player.getX() + (player.getRandom().nextFloat() - 0.5) * 2,
                            player.getY(),
                            player.getZ() + (player.getRandom().nextFloat() - 0.5) * 2,
                            0, -0.05, 0);
                }
            }

            return TypedActionResult.success(stack, world.isClient());
        }

        return TypedActionResult.pass(stack);
    }

    private void applyGlidePhysics(PlayerEntity player, World world) {
        float yawRad = player.getYaw() * ((float) Math.PI / 180f);
        float pitchRad = player.getPitch() * ((float) Math.PI / 180f);

        // Base glide vector
        double forwardX = -MathHelper.sin(yawRad) * GLIDE_SPEED;
        double forwardZ = MathHelper.cos(yawRad) * GLIDE_SPEED;

        // Pitch affects vertical: looking down = speed, looking up = lift
        double verticalComponent = -MathHelper.sin(pitchRad) * LIFT_FACTOR;

        Vec3d currentVelocity = player.getVelocity();

        // Apply wind current boost
        Vec3d windBoost = WindCurrentSystem.getWindAt(player.getBlockPos(), world);

        // Combine: keep some momentum, add glide force, reduce gravity
        Vec3d newVelocity = new Vec3d(
                currentVelocity.x * 0.5 + forwardX * 0.3 + windBoost.x,
                Math.max(currentVelocity.y, -0.1) + verticalComponent + GRAVITY_REDUCTION + windBoost.y,
                currentVelocity.z * 0.5 + forwardZ * 0.3 + windBoost.z
        );

        // Cap speed
        if (newVelocity.horizontalLength() > 1.5) {
            double scale = 1.5 / newVelocity.horizontalLength();
            newVelocity = new Vec3d(newVelocity.x * scale, newVelocity.y, newVelocity.z * scale);
        }

        player.setVelocity(newVelocity);
        player.velocityModified = true;
        player.fallDistance = 0; // No fall damage while gliding

        // Wind current particles when boosted
        if (windBoost.length() > 0.01 && world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0, 0, 0, 0);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // Can hold indefinitely
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
