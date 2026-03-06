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
 * Wisteria Tree — cascading purple/lavender vine curtains hanging from
 * a spreading canopy. Iconic in Japanese gardens and Ghibli backgrounds.
 * Creates a magical tunnel-like canopy effect with long hanging vines.
 */
public class WisteriaTreeFeature extends Feature<DefaultFeatureConfig> {
    public WisteriaTreeFeature(Codec<DefaultFeatureConfig> codec) {
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

        int trunkHeight = 5 + random.nextInt(4);
        BlockState log = Blocks.OAK_LOG.getDefaultState();
        BlockState leaves = Blocks.FLOWERING_AZALEA_LEAVES.getDefaultState();

        // Gnarled trunk
        for (int y = 0; y < trunkHeight; y++) {
            int dx = (int) (Math.sin(y * 0.5) * 0.5);
            int dz = (int) (Math.cos(y * 0.5) * 0.5);
            world.setBlockState(origin.add(dx, y, dz), log, 3);
            if (y < 3) {
                // Thicker base
                world.setBlockState(origin.add(dx + 1, y, dz), log, 3);
                world.setBlockState(origin.add(dx, y, dz + 1), log, 3);
            }
        }

        BlockPos canopyCenter = origin.up(trunkHeight);
        int canopyRadius = 5 + random.nextInt(3);

        // Spreading branches
        int branchCount = 5 + random.nextInt(3);
        for (int b = 0; b < branchCount; b++) {
            double angle = (Math.PI * 2 * b) / branchCount;
            int branchLen = canopyRadius - random.nextInt(2);

            for (int step = 0; step < branchLen; step++) {
                int bx = (int) (Math.cos(angle) * step);
                int bz = (int) (Math.sin(angle) * step);
                int by = step > 2 ? -1 : 0; // Slight droop
                world.setBlockState(canopyCenter.add(bx, by, bz), log, 3);
            }
        }

        // Leaf canopy (thinner — the vines are the main visual)
        for (int x = -canopyRadius; x <= canopyRadius; x++) {
            for (int z = -canopyRadius; z <= canopyRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist <= canopyRadius + random.nextFloat() - 0.5) {
                    BlockPos leafPos = canopyCenter.add(x, 0, z);
                    if (world.getBlockState(leafPos).isAir()) {
                        world.setBlockState(leafPos, leaves, 3);
                    }
                    // Occasional top layer
                    if (random.nextInt(3) == 0) {
                        BlockPos topLeaf = canopyCenter.add(x, 1, z);
                        if (world.getBlockState(topLeaf).isAir()) {
                            world.setBlockState(topLeaf, leaves, 3);
                        }
                    }
                }
            }
        }

        // THE MAIN EVENT: Cascading wisteria vines!
        for (int x = -canopyRadius; x <= canopyRadius; x++) {
            for (int z = -canopyRadius; z <= canopyRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);

                // More vines at edges (curtain effect)
                boolean isEdge = dist > canopyRadius - 2;
                int vineChance = isEdge ? 2 : 5;

                if (random.nextInt(vineChance) != 0) continue;
                if (dist > canopyRadius) continue;

                BlockPos vineStart = canopyCenter.add(x, -1, z);
                if (!world.getBlockState(vineStart.up()).isAir()) {
                    // Hang vines
                    int vineLength = isEdge ? (3 + random.nextInt(5)) : (1 + random.nextInt(3));
                    for (int v = 0; v < vineLength; v++) {
                        BlockPos vinePos = vineStart.down(v);
                        if (!world.getBlockState(vinePos).isAir()) break;

                        // Alternate between leaves and vine for color
                        if (v % 2 == 0 || random.nextInt(3) == 0) {
                            world.setBlockState(vinePos, leaves, 3);
                        } else {
                            world.setBlockState(vinePos, Blocks.VINE.getDefaultState(), 3);
                        }
                    }
                }
            }
        }

        // Scatter purple carpet around base
        for (int x = -canopyRadius; x <= canopyRadius; x++) {
            for (int z = -canopyRadius; z <= canopyRadius; z++) {
                if (random.nextInt(5) != 0) continue;
                for (int y = origin.getY() + trunkHeight; y >= origin.getY() - 3; y--) {
                    BlockPos check = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (!world.getBlockState(check).isAir() && world.getBlockState(check.up()).isAir()) {
                        if (random.nextInt(2) == 0) {
                            world.setBlockState(check.up(), Blocks.PINK_PETALS.getDefaultState(), 3);
                        }
                        break;
                    }
                }
            }
        }

        return true;
    }
}
