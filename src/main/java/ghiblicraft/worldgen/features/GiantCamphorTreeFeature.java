package ghiblicraft.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GiantCamphorTreeFeature extends Feature<DefaultFeatureConfig> {
    public GiantCamphorTreeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        // Check if there's solid ground
        if (!world.getBlockState(origin.down()).isOpaqueFullCube(world, origin.down())) {
            return false;
        }

        int trunkHeight = 20 + random.nextInt(15); // 20-34 blocks tall
        int trunkRadius = 3 + random.nextInt(2);   // 3-4 block radius

        // Generate massive trunk with buttress roots
        generateTrunk(world, origin, trunkHeight, trunkRadius, random);

        // Generate root system
        generateRoots(world, origin, trunkRadius, random);

        // Generate canopy
        generateCanopy(world, origin.up(trunkHeight), trunkHeight, random);

        // Hollow interior (like Totoro's tree)
        generateHollowInterior(world, origin, trunkHeight, trunkRadius, random);

        // Decorative elements
        generateHangingVines(world, origin.up(trunkHeight), random);
        generateGlowLichens(world, origin, trunkHeight, trunkRadius, random);

        return true;
    }

    private void generateTrunk(StructureWorldAccess world, BlockPos origin, int height, int radius, Random random) {
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();
        BlockState mossyCobblestone = Blocks.MOSSY_COBBLESTONE.getDefaultState();

        for (int y = 0; y < height; y++) {
            // Trunk narrows as it goes up
            float currentRadius = radius * (1.0f - (y * 0.3f / height));
            if (currentRadius < 1.0f) currentRadius = 1.0f;

            for (int x = -(int) currentRadius - 1; x <= (int) currentRadius + 1; x++) {
                for (int z = -(int) currentRadius - 1; z <= (int) currentRadius + 1; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= currentRadius + (random.nextFloat() * 0.5f)) {
                        BlockPos pos = origin.add(x, y, z);
                        if (y < 2 && dist > currentRadius - 0.5f) {
                            world.setBlockState(pos, mossyCobblestone, 3);
                        } else {
                            world.setBlockState(pos, oakLog, 3);
                        }
                    }
                }
            }
        }
    }

    private void generateRoots(StructureWorldAccess world, BlockPos origin, int trunkRadius, Random random) {
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();
        int rootCount = 5 + random.nextInt(4);

        for (int i = 0; i < rootCount; i++) {
            double angle = (Math.PI * 2 * i) / rootCount + random.nextGaussian() * 0.3;
            int rootLength = 6 + random.nextInt(6);

            for (int step = 0; step < rootLength; step++) {
                int x = (int) (Math.cos(angle) * (trunkRadius + step));
                int z = (int) (Math.sin(angle) * (trunkRadius + step));
                int y = -(step / 3); // Roots go down gradually

                BlockPos rootPos = origin.add(x, y, z);
                world.setBlockState(rootPos, oakLog, 3);

                // Thicken root near trunk
                if (step < rootLength / 2) {
                    world.setBlockState(rootPos.up(), oakLog, 3);
                }
            }
        }
    }

    private void generateCanopy(StructureWorldAccess world, BlockPos top, int trunkHeight, Random random) {
        BlockState oakLeaves = Blocks.OAK_LEAVES.getDefaultState();
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        // Multiple layers of canopy
        int canopyRadius = 8 + random.nextInt(5);
        int canopyLayers = 6 + random.nextInt(4);

        // Generate major branches first
        int branchCount = 5 + random.nextInt(4);
        for (int b = 0; b < branchCount; b++) {
            double angle = (Math.PI * 2 * b) / branchCount;
            int branchLength = canopyRadius - 2 + random.nextInt(3);
            int branchY = -(random.nextInt(5));

            for (int step = 0; step < branchLength; step++) {
                int x = (int) (Math.cos(angle) * step);
                int z = (int) (Math.sin(angle) * step);
                int y = branchY + (step / 4);

                BlockPos branchPos = top.add(x, y, z);
                world.setBlockState(branchPos, oakLog, 3);
            }
        }

        // Generate leaf dome
        for (int y = -canopyLayers / 2; y <= canopyLayers; y++) {
            float layerRadius = canopyRadius * (1.0f - Math.abs(y - canopyLayers * 0.3f) / (canopyLayers * 0.8f));
            if (layerRadius < 1) continue;

            for (int x = -(int) layerRadius - 1; x <= (int) layerRadius + 1; x++) {
                for (int z = -(int) layerRadius - 1; z <= (int) layerRadius + 1; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= layerRadius + (random.nextFloat() - 0.5f) * 2) {
                        BlockPos leafPos = top.add(x, y, z);
                        if (world.getBlockState(leafPos).isAir()) {
                            world.setBlockState(leafPos, oakLeaves, 3);
                        }
                    }
                }
            }
        }
    }

    private void generateHollowInterior(StructureWorldAccess world, BlockPos origin, int height, int radius, Random random) {
        // Create a cozy hollow in the trunk base
        int hollowHeight = Math.min(8, height / 3);
        int hollowRadius = Math.max(1, radius - 1);

        for (int y = 1; y < hollowHeight; y++) {
            for (int x = -hollowRadius; x <= hollowRadius; x++) {
                for (int z = -hollowRadius; z <= hollowRadius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist < hollowRadius) {
                        BlockPos pos = origin.add(x, y, z);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                }
            }
        }

        // Add moss carpet floor
        for (int x = -hollowRadius; x <= hollowRadius; x++) {
            for (int z = -hollowRadius; z <= hollowRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist < hollowRadius) {
                    BlockPos floorPos = origin.add(x, 1, z);
                    world.setBlockState(floorPos, Blocks.MOSS_CARPET.getDefaultState(), 3);
                }
            }
        }

        // Entrance on one side
        int entranceDir = random.nextInt(4);
        int dx = entranceDir == 0 ? 1 : entranceDir == 1 ? -1 : 0;
        int dz = entranceDir == 2 ? 1 : entranceDir == 3 ? -1 : 0;

        for (int y = 1; y <= 3; y++) {
            for (int step = hollowRadius; step <= radius + 1; step++) {
                BlockPos doorPos = origin.add(dx * step, y, dz * step);
                world.setBlockState(doorPos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        // Place a glow lichen inside for atmosphere
        world.setBlockState(origin.add(0, hollowHeight - 1, 0), Blocks.SHROOMLIGHT.getDefaultState(), 3);
    }

    private void generateHangingVines(StructureWorldAccess world, BlockPos top, Random random) {
        int radius = 10;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (random.nextInt(4) != 0) continue;

                BlockPos checkPos = top.add(x, -2, z);
                if (world.getBlockState(checkPos).isOf(Blocks.OAK_LEAVES) &&
                        world.getBlockState(checkPos.down()).isAir()) {
                    int vineLength = 2 + random.nextInt(6);
                    for (int v = 1; v <= vineLength; v++) {
                        BlockPos vinePos = checkPos.down(v);
                        if (!world.getBlockState(vinePos).isAir()) break;
                        world.setBlockState(vinePos, Blocks.VINE.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    private void generateGlowLichens(StructureWorldAccess world, BlockPos origin, int height, int radius, Random random) {
        for (int i = 0; i < 15; i++) {
            int y = random.nextInt(height / 2);
            double angle = random.nextFloat() * Math.PI * 2;
            int x = (int) (Math.cos(angle) * (radius + 1));
            int z = (int) (Math.sin(angle) * (radius + 1));

            BlockPos lichPos = origin.add(x, y, z);
            if (world.getBlockState(lichPos).isAir()) {
                world.setBlockState(lichPos, Blocks.GLOW_LICHEN.getDefaultState(), 3);
            }
        }
    }
}
