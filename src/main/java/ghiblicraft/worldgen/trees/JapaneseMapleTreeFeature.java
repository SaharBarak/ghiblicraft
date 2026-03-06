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
 * Japanese Maple (Momiji) — smaller ornamental tree with brilliant red/orange leaves.
 * Often found near water, paths, and shrines.
 * Irregular, artistic shape with layered horizontal branches.
 */
public class JapaneseMapleTreeFeature extends Feature<DefaultFeatureConfig> {
    public JapaneseMapleTreeFeature(Codec<DefaultFeatureConfig> codec) {
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

        int trunkHeight = 3 + random.nextInt(3); // 3-5, smaller ornamental tree
        BlockState log = Blocks.DARK_OAK_LOG.getDefaultState();
        // Use red/orange colored leaves (azalea for red tone)
        BlockState leaves = Blocks.AZALEA_LEAVES.getDefaultState();

        // Curved trunk (Japanese maples often have artistic bends)
        int bendDir = random.nextInt(4);
        for (int y = 0; y < trunkHeight; y++) {
            int dx = 0, dz = 0;
            if (y > trunkHeight / 2) {
                switch (bendDir) {
                    case 0 -> dx = 1;
                    case 1 -> dx = -1;
                    case 2 -> dz = 1;
                    case 3 -> dz = -1;
                }
            }
            world.setBlockState(origin.add(dx, y, dz), log, 3);
        }

        // Layered horizontal branch structure (3-4 distinct layers)
        int layers = 2 + random.nextInt(2);
        for (int layer = 0; layer < layers; layer++) {
            int layerY = trunkHeight - layer * 2 + random.nextInt(2);
            if (layerY < 2) continue;

            float layerRadius = 2.5f + random.nextFloat() * 2;
            if (layer > 0) layerRadius *= 0.8f; // Lower layers slightly smaller

            BlockPos layerCenter = origin.up(layerY);

            // Branches reaching outward
            int branchCount = 3 + random.nextInt(3);
            for (int b = 0; b < branchCount; b++) {
                double angle = (Math.PI * 2 * b) / branchCount + random.nextGaussian() * 0.3;
                int branchLen = (int) layerRadius;
                for (int step = 1; step <= branchLen; step++) {
                    int bx = (int) (Math.cos(angle) * step);
                    int bz = (int) (Math.sin(angle) * step);
                    world.setBlockState(layerCenter.add(bx, 0, bz), log, 3);
                }
            }

            // Flat leaf layer (Japanese maples have horizontal layered canopy)
            for (int x = -(int) layerRadius - 1; x <= (int) layerRadius + 1; x++) {
                for (int z = -(int) layerRadius - 1; z <= (int) layerRadius + 1; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= layerRadius + random.nextFloat()) {
                        for (int dy = 0; dy <= 1; dy++) {
                            BlockPos leafPos = layerCenter.add(x, dy, z);
                            if (world.getBlockState(leafPos).isAir()) {
                                world.setBlockState(leafPos, leaves, 3);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
