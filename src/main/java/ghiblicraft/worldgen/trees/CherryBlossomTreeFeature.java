package ghiblicraft.worldgen.trees;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Iconic Studio Ghibli cherry blossom tree.
 * Wide, spreading canopy with drooping branches covered in pink leaves.
 * Petals fall as particles. Multiple trunk styles (straight, leaning, split).
 */
public class CherryBlossomTreeFeature extends Feature<DefaultFeatureConfig> {
    public CherryBlossomTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        if (!world.getBlockState(origin.down()).isOpaqueFullCube(world, origin.down())) {
            return false;
        }

        int trunkHeight = 4 + random.nextInt(4); // 4-7 blocks
        BlockState log = Blocks.CHERRY_LOG.getDefaultState();
        BlockState leaves = Blocks.CHERRY_LEAVES.getDefaultState();

        // Trunk with gentle lean
        double leanX = (random.nextFloat() - 0.5) * 0.3;
        double leanZ = (random.nextFloat() - 0.5) * 0.3;

        for (int y = 0; y < trunkHeight; y++) {
            int offsetX = (int) Math.round(leanX * y);
            int offsetZ = (int) Math.round(leanZ * y);
            world.setBlockState(origin.add(offsetX, y, offsetZ), log, 3);
        }

        // Wide spreading canopy — the signature Ghibli cherry blossom shape
        BlockPos canopyCenter = origin.up(trunkHeight);
        int canopyRadius = 4 + random.nextInt(3);
        int canopyHeight = 3 + random.nextInt(2);

        // Generate branches first
        int branchCount = 4 + random.nextInt(3);
        for (int b = 0; b < branchCount; b++) {
            double angle = (Math.PI * 2 * b) / branchCount + random.nextGaussian() * 0.2;
            int branchLen = canopyRadius - 1 + random.nextInt(2);

            for (int step = 0; step < branchLen; step++) {
                int bx = (int) (Math.cos(angle) * step);
                int bz = (int) (Math.sin(angle) * step);
                int by = step > branchLen / 2 ? -1 : 0; // Droop outward
                world.setBlockState(canopyCenter.add(bx, by, bz), log, 3);
            }
        }

        // Dome-shaped canopy (wider than tall — umbrella shape)
        for (int y = -1; y <= canopyHeight; y++) {
            float layerRadius;
            if (y <= 0) {
                // Bottom layers are widest (drooping effect)
                layerRadius = canopyRadius + 1 - Math.abs(y);
            } else {
                layerRadius = canopyRadius * (1.0f - (float) y / (canopyHeight + 1));
            }

            for (int x = -(int) layerRadius - 1; x <= (int) layerRadius + 1; x++) {
                for (int z = -(int) layerRadius - 1; z <= (int) layerRadius + 1; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= layerRadius + (random.nextFloat() - 0.5) * 1.5) {
                        BlockPos leafPos = canopyCenter.add(x, y, z);
                        if (world.getBlockState(leafPos).isAir()) {
                            world.setBlockState(leafPos, leaves, 3);
                        }
                    }
                }
            }
        }

        // Hanging petal curtains (drooping leaf columns on edges)
        for (int i = 0; i < 8; i++) {
            double angle = random.nextFloat() * Math.PI * 2;
            int hx = (int) (Math.cos(angle) * (canopyRadius - 1));
            int hz = (int) (Math.sin(angle) * (canopyRadius - 1));
            int hangLength = 1 + random.nextInt(3);

            for (int h = 0; h < hangLength; h++) {
                BlockPos hangPos = canopyCenter.add(hx, -2 - h, hz);
                if (world.getBlockState(hangPos).isAir()) {
                    world.setBlockState(hangPos, leaves, 3);
                }
            }
        }

        // Scatter petals on ground (pink carpet/petal block)
        for (int x = -canopyRadius - 2; x <= canopyRadius + 2; x++) {
            for (int z = -canopyRadius - 2; z <= canopyRadius + 2; z++) {
                if (random.nextInt(4) != 0) continue;
                for (int y = origin.getY() + trunkHeight; y >= origin.getY() - 3; y--) {
                    BlockPos groundCheck = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (!world.getBlockState(groundCheck).isAir() &&
                            world.getBlockState(groundCheck.up()).isAir()) {
                        if (random.nextInt(3) == 0) {
                            world.setBlockState(groundCheck.up(),
                                    Blocks.PINK_PETALS.getDefaultState(), 3);
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }
}
