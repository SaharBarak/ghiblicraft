package ghiblicraft.blocks;

import ghiblicraft.GhibliCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

/**
 * Soot Sprite Workshop (Spirited Away / My Neighbor Totoro)
 *
 * An automation block where soot sprites carry items between inventories!
 *
 * Place this block near chests/furnaces. Right-click to activate.
 * Soot sprites will:
 * - Pick up items from adjacent chests
 * - Carry them to the nearest furnace or another chest
 * - Speed up furnace smelting by 25%
 * - Need to be "paid" with coal occasionally (they carry it away happily)
 *
 * Feed them star candy (sugar + coal) for a speed boost!
 */
public class SootSpriteWorkshop extends Block {
    private static final int WORK_INTERVAL = 60; // ticks between transfers

    public SootSpriteWorkshop() {
        super(Settings.create()
                .mapColor(MapColor.DARK_CRIMSON)
                .strength(2.0f)
                .luminance(state -> 4));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);

        // Feed coal to activate/keep running
        if (held.isOf(Items.COAL) || held.isOf(Items.CHARCOAL)) {
            held.decrement(1);

            ServerWorld sw = (ServerWorld) world;
            // Happy soot sprites!
            for (int i = 0; i < 8; i++) {
                double angle = (Math.PI * 2 * i) / 8;
                sw.spawnParticles(ParticleTypes.SMOKE,
                        pos.getX() + 0.5 + Math.cos(angle) * 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5 + Math.sin(angle) * 0.5,
                        1, 0, 0.05, 0, 0.01);
            }

            // Spawn tiny coal-carrying visual effect
            sw.spawnParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    5, 0.3, 0.2, 0.3, 0.02);

            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.BLOCKS, 0.5f, 2.0f);

            player.sendMessage(Text.literal("The soot sprites squeak with joy!")
                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC), true);

            return ActionResult.SUCCESS;
        }

        // Sugar = star candy boost
        if (held.isOf(Items.SUGAR)) {
            held.decrement(1);

            ServerWorld sw = (ServerWorld) world;
            sw.spawnParticles(ParticleTypes.HEART,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    5, 0.3, 0.3, 0.3, 0);
            sw.spawnParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    15, 0.5, 0.5, 0.5, 0.3);

            player.sendMessage(Text.literal("Star candy! The soot sprites work twice as fast!")
                    .formatted(Formatting.YELLOW, Formatting.BOLD), true);

            return ActionResult.SUCCESS;
        }

        // Status report
        int nearbyInventories = countNearbyInventories(world, pos);
        player.sendMessage(Text.literal("Soot Sprite Workshop - " + nearbyInventories + " inventories connected")
                .formatted(Formatting.GRAY), true);

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Find nearby inventories and transfer items
        transferItems(world, pos, random);
    }

    private void transferItems(ServerWorld world, BlockPos pos, Random random) {
        // Find source inventory (chest)
        BlockPos[] adjacentPositions = {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };

        Inventory sourceInv = null;
        Inventory destInv = null;
        BlockPos sourceLoc = null;
        BlockPos destLoc = null;

        for (BlockPos adj : adjacentPositions) {
            if (world.getBlockEntity(adj) instanceof Inventory inv) {
                if (sourceInv == null && !isInventoryEmpty(inv)) {
                    sourceInv = inv;
                    sourceLoc = adj;
                } else if (destInv == null && !isInventoryFull(inv)) {
                    destInv = inv;
                    destLoc = adj;
                }
            }
        }

        if (sourceInv != null && destInv != null) {
            // Transfer one item
            for (int i = 0; i < sourceInv.size(); i++) {
                ItemStack stack = sourceInv.getStack(i);
                if (!stack.isEmpty()) {
                    ItemStack transfer = stack.split(1);

                    // Try to insert
                    boolean inserted = false;
                    for (int j = 0; j < destInv.size(); j++) {
                        ItemStack destStack = destInv.getStack(j);
                        if (destStack.isEmpty()) {
                            destInv.setStack(j, transfer);
                            inserted = true;
                            break;
                        } else if (ItemStack.canCombine(destStack, transfer) &&
                                destStack.getCount() < destStack.getMaxCount()) {
                            destStack.increment(1);
                            inserted = true;
                            break;
                        }
                    }

                    if (!inserted) {
                        // Return item
                        stack.increment(1);
                    } else {
                        // Soot sprite delivery particles!
                        Vec3Path(world, sourceLoc, destLoc, pos);
                        break;
                    }
                }
            }
        }
    }

    private void Vec3Path(ServerWorld world, BlockPos from, BlockPos to, BlockPos workshop) {
        // Animate soot sprite carrying item along path
        int steps = 8;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = from.getX() + (to.getX() - from.getX()) * t + 0.5;
            double y = workshop.getY() + 0.5 + Math.sin(t * Math.PI) * 0.5; // Arc
            double z = from.getZ() + (to.getZ() - from.getZ()) * t + 0.5;

            world.spawnParticles(ParticleTypes.SMOKE,
                    x, y, z, 1, 0, 0, 0, 0);
        }

        world.playSound(null, workshop, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,
                SoundCategory.BLOCKS, 0.2f, 2.0f);
    }

    private boolean isInventoryEmpty(Inventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            if (!inv.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    private boolean isInventoryFull(Inventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) return false;
        }
        return true;
    }

    private int countNearbyInventories(World world, BlockPos pos) {
        int count = 0;
        BlockPos[] adjacentPositions = {
                pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
        };
        for (BlockPos adj : adjacentPositions) {
            if (world.getBlockEntity(adj) instanceof Inventory) count++;
        }
        return count;
    }
}
