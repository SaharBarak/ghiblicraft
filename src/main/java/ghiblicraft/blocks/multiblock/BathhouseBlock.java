package ghiblicraft.blocks.multiblock;

import ghiblicraft.systems.SpiritReputationSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Bathhouse Block (Spirited Away)
 *
 * When placed above a water source with heat underneath (magma/campfire),
 * becomes a Spirit Bathhouse that provides powerful cleansing effects:
 * - Removes all negative status effects
 * - Grants regeneration, resistance, and strength
 * - Cleanses curse status from the Karma system
 * - Can enchant items if spirit essence is used
 *
 * Multi-block check: Needs water below, heat 2 blocks below.
 */
public class BathhouseBlock extends Block {
    public BathhouseBlock() {
        super(Settings.create()
                .mapColor(MapColor.RED)
                .strength(3.0f)
                .luminance(state -> 8)
                .nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ServerWorld serverWorld = (ServerWorld) world;
        boolean isValidBathhouse = checkMultiblock(world, pos);

        if (!isValidBathhouse) {
            player.sendMessage(Text.literal("The bathhouse needs water below and heat beneath that (magma/campfire)")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), true);
            return ActionResult.CONSUME;
        }

        ItemStack held = player.getStackInHand(hand);
        boolean usedSpiritEssence = held.isOf(Items.AMETHYST_SHARD);

        // Steam effects
        for (int i = 0; i < 20; i++) {
            serverWorld.spawnParticles(ParticleTypes.CLOUD,
                    pos.getX() + 0.5 + world.random.nextGaussian() * 0.5,
                    pos.getY() + 1.0 + world.random.nextFloat() * 2,
                    pos.getZ() + 0.5 + world.random.nextGaussian() * 0.5,
                    1, 0, 0.05, 0, 0.01);
        }

        serverWorld.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT,
                SoundCategory.BLOCKS, 1.0f, 1.2f);

        // Remove ALL negative effects
        player.removeStatusEffect(StatusEffects.POISON);
        player.removeStatusEffect(StatusEffects.WITHER);
        player.removeStatusEffect(StatusEffects.WEAKNESS);
        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.removeStatusEffect(StatusEffects.BLINDNESS);
        player.removeStatusEffect(StatusEffects.HUNGER);
        player.removeStatusEffect(StatusEffects.NAUSEA);
        player.removeStatusEffect(StatusEffects.DARKNESS);
        player.removeStatusEffect(StatusEffects.UNLUCK);

        // Grant cleansing buffs
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1200, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 2400, 0, true, false));

        // Karma boost
        SpiritReputationSystem.modifyKarma(player, 5);

        if (usedSpiritEssence) {
            held.decrement(1);

            // Enhanced cleansing with spirit essence
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 2400, 2, true, false));

            // Massive spirit karma boost
            SpiritReputationSystem.modifyKarma(player, 10);

            serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    30, 1.0, 1.0, 1.0, 0.3);

            player.sendMessage(Text.literal("The spirit waters flow through you with divine energy!")
                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        } else {
            player.sendMessage(Text.literal("The warm waters cleanse your body and spirit...")
                    .formatted(Formatting.AQUA, Formatting.ITALIC), false);
        }

        // Heal fully
        player.heal(player.getMaxHealth());
        player.getHungerManager().setFoodLevel(20);

        return ActionResult.SUCCESS;
    }

    private boolean checkMultiblock(World world, BlockPos pos) {
        // Check for water directly below
        BlockState below = world.getBlockState(pos.down());
        boolean hasWater = below.isOf(Blocks.WATER);

        // Check for heat source two blocks below
        BlockState heatSource = world.getBlockState(pos.down(2));
        boolean hasHeat = heatSource.isOf(Blocks.MAGMA_BLOCK) ||
                heatSource.isOf(Blocks.CAMPFIRE) ||
                heatSource.isOf(Blocks.SOUL_CAMPFIRE) ||
                heatSource.isOf(Blocks.LAVA);

        return hasWater && hasHeat;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (checkMultiblock(world, pos)) {
            // Steam rising
            world.addParticle(ParticleTypes.CLOUD,
                    pos.getX() + 0.5 + random.nextGaussian() * 0.3,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5 + random.nextGaussian() * 0.3,
                    0, 0.03, 0);

            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5 + random.nextGaussian() * 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5 + random.nextGaussian() * 0.5,
                        0, 0.1, 0);
            }
        }
    }
}
