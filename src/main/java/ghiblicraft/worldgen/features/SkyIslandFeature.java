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

public class SkyIslandFeature extends Feature<DefaultFeatureConfig> {
    public SkyIslandFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        // Sky islands float between y=150 and y=220
        int islandY = 150 + random.nextInt(70);
        BlockPos islandCenter = new BlockPos(origin.getX(), islandY, origin.getZ());

        int islandRadius = 12 + random.nextInt(10); // 12-21 block radius
        int islandThickness = 4 + random.nextInt(4);

        // Generate floating island shape
        generateIslandBody(world, islandCenter, islandRadius, islandThickness, random);

        // Add surface features
        generateSurface(world, islandCenter, islandRadius, random);

        // Add hanging roots and stalactites underneath
        generateUnderbelly(world, islandCenter, islandRadius, islandThickness, random);

        // Add a small ruin on top (Laputa style)
        if (random.nextInt(3) == 0) {
            generateRuins(world, islandCenter, islandRadius, random);
        }

        // Add waterfall cascading off the edge
        if (random.nextInt(2) == 0) {
            generateWaterfall(world, islandCenter, islandRadius, islandThickness, random);
        }

        return true;
    }

    private void generateIslandBody(StructureWorldAccess world, BlockPos center, int radius,
                                     int thickness, Random random) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius) continue;

                // Vary thickness based on distance from center (thicker in middle)
                int localThickness = (int) (thickness * (1.0 - (dist / radius) * 0.7));
                localThickness = Math.max(1, localThickness + random.nextInt(2) - 1);

                for (int y = -localThickness; y <= 0; y++) {
                    BlockPos pos = center.add(x, y, z);
                    if (y == 0) {
                        world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                    } else if (y > -localThickness + 1) {
                        world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
                    } else {
                        world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    private void generateSurface(StructureWorldAccess world, BlockPos center, int radius, Random random) {
        // Grass, flowers, and a few trees on top
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius - 1) continue;

                BlockPos surfacePos = center.add(x, 1, z);

                if (random.nextInt(15) == 0 && dist < radius - 3) {
                    // Small tree
                    generateSmallTree(world, surfacePos, random);
                } else if (random.nextInt(6) == 0) {
                    // Flowers and grass
                    Block[] vegetation = {
                            Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.SHORT_GRASS,
                            Blocks.DANDELION, Blocks.POPPY, Blocks.AZURE_BLUET,
                            Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY,
                            Blocks.TALL_GRASS, Blocks.FERN
                    };
                    world.setBlockState(surfacePos, vegetation[random.nextInt(vegetation.length)].getDefaultState(), 3);
                }
            }
        }

        // Central ancient tree
        if (random.nextInt(2) == 0) {
            generateSmallTree(world, center.up(), random);
        }
    }

    private void generateSmallTree(StructureWorldAccess world, BlockPos base, Random random) {
        int height = 4 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            world.setBlockState(base.up(y), Blocks.OAK_LOG.getDefaultState(), 3);
        }

        // Leaf sphere
        int leafRadius = 2 + random.nextInt(2);
        BlockPos leafCenter = base.up(height);
        for (int x = -leafRadius; x <= leafRadius; x++) {
            for (int y = -1; y <= leafRadius; y++) {
                for (int z = -leafRadius; z <= leafRadius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist <= leafRadius + random.nextFloat() * 0.5f) {
                        BlockPos leafPos = leafCenter.add(x, y, z);
                        if (world.getBlockState(leafPos).isAir()) {
                            world.setBlockState(leafPos, Blocks.OAK_LEAVES.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
    }

    private void generateUnderbelly(StructureWorldAccess world, BlockPos center, int radius,
                                     int thickness, Random random) {
        // Hanging vines and roots underneath
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > radius || random.nextInt(5) != 0) continue;

                int hangLength = 2 + random.nextInt(8);
                BlockPos hangStart = center.add(x, -thickness - 1, z);

                for (int i = 0; i < hangLength; i++) {
                    BlockPos vinePos = hangStart.down(i);
                    if (random.nextInt(3) == 0) {
                        world.setBlockState(vinePos, Blocks.HANGING_ROOTS.getDefaultState(), 3);
                    } else {
                        world.setBlockState(vinePos, Blocks.VINE.getDefaultState(), 3);
                    }
                }
            }
        }

        // Glowstone clusters underneath for ethereal lighting
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(radius) - radius / 2;
            int z = random.nextInt(radius) - radius / 2;
            BlockPos glowPos = center.add(x, -thickness, z);
            world.setBlockState(glowPos, Blocks.SHROOMLIGHT.getDefaultState(), 3);
        }
    }

    private void generateRuins(StructureWorldAccess world, BlockPos center, int radius, Random random) {
        // Small stone ruin structure (Laputa-inspired)
        int ruinSize = 3 + random.nextInt(3);
        BlockPos ruinBase = center.add(random.nextInt(5) - 2, 1, random.nextInt(5) - 2);

        // Crumbling walls
        for (int x = -ruinSize; x <= ruinSize; x++) {
            for (int z = -ruinSize; z <= ruinSize; z++) {
                boolean isWall = Math.abs(x) == ruinSize || Math.abs(z) == ruinSize;
                if (!isWall) continue;

                int wallHeight = 2 + random.nextInt(3);
                // Crumble effect - some sections missing
                if (random.nextInt(3) == 0) continue;

                for (int y = 0; y < wallHeight; y++) {
                    if (random.nextInt(4) == 0) continue; // Random gaps
                    BlockPos wallPos = ruinBase.add(x, y, z);
                    BlockState block = random.nextInt(3) == 0 ?
                            Blocks.MOSSY_STONE_BRICKS.getDefaultState() :
                            Blocks.STONE_BRICKS.getDefaultState();
                    world.setBlockState(wallPos, block, 3);
                }
            }
        }

        // Central pedestal with a light source
        world.setBlockState(ruinBase, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 3);
        world.setBlockState(ruinBase.up(), Blocks.SEA_LANTERN.getDefaultState(), 3);

        // Vines on the ruins
        for (int x = -ruinSize; x <= ruinSize; x++) {
            for (int z = -ruinSize; z <= ruinSize; z++) {
                if (random.nextInt(4) != 0) continue;
                for (int y = 0; y < 4; y++) {
                    BlockPos checkPos = ruinBase.add(x, y, z);
                    if (world.getBlockState(checkPos).isAir() && random.nextInt(2) == 0) {
                        world.setBlockState(checkPos, Blocks.VINE.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    private void generateWaterfall(StructureWorldAccess world, BlockPos center, int radius,
                                    int thickness, Random random) {
        // Place water at the edge that flows down
        double angle = random.nextFloat() * Math.PI * 2;
        int x = (int) (Math.cos(angle) * (radius - 1));
        int z = (int) (Math.sin(angle) * (radius - 1));

        BlockPos waterPos = center.add(x, 0, z);
        world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 3);
    }
}
