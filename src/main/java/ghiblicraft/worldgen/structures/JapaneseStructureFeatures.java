package ghiblicraft.worldgen.structures;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Generates scattered Japanese structures throughout the world:
 * - Torii Gates (red gateway arches)
 * - Stone Lanterns (ishidoro)
 * - Small Mountain Shrines
 * - Wooden Bridges over water
 * - Hot Spring Onsen pools
 * - Zen Garden clearings
 */
public class JapaneseStructureFeatures extends Feature<DefaultFeatureConfig> {
    public JapaneseStructureFeatures(Codec<DefaultFeatureConfig> codec) {
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

        int structureType = random.nextInt(6);
        return switch (structureType) {
            case 0 -> generateToriiGate(world, origin, random);
            case 1 -> generateStoneLantern(world, origin, random);
            case 2 -> generateShrine(world, origin, random);
            case 3 -> generateWoodenBridge(world, origin, random);
            case 4 -> generateOnsen(world, origin, random);
            case 5 -> generateZenGarden(world, origin, random);
            default -> false;
        };
    }

    private boolean generateToriiGate(StructureWorldAccess world, BlockPos origin, Random random) {
        BlockState redConcrete = Blocks.RED_CONCRETE.getDefaultState();
        BlockState blackWool = Blocks.BLACK_CONCRETE.getDefaultState();

        int gateHeight = 5;
        int gateWidth = 5;

        // Two pillars
        for (int y = 0; y < gateHeight; y++) {
            world.setBlockState(origin.add(-gateWidth / 2, y, 0), redConcrete, 3);
            world.setBlockState(origin.add(gateWidth / 2, y, 0), redConcrete, 3);
        }

        // Top crossbeam (kasagi) — extends beyond pillars
        for (int x = -gateWidth / 2 - 1; x <= gateWidth / 2 + 1; x++) {
            world.setBlockState(origin.add(x, gateHeight, 0), blackWool, 3);
            world.setBlockState(origin.add(x, gateHeight + 1, 0), redConcrete, 3);
        }

        // Lower crossbeam (nuki)
        for (int x = -gateWidth / 2; x <= gateWidth / 2; x++) {
            world.setBlockState(origin.add(x, gateHeight - 2, 0), redConcrete, 3);
        }

        // Stone base
        world.setBlockState(origin.add(-gateWidth / 2, -1, 0), Blocks.STONE.getDefaultState(), 3);
        world.setBlockState(origin.add(gateWidth / 2, -1, 0), Blocks.STONE.getDefaultState(), 3);

        // Path through gate
        for (int z = -3; z <= 3; z++) {
            for (int x = -1; x <= 1; x++) {
                BlockPos pathPos = origin.add(x, -1, z);
                if (world.getBlockState(pathPos).isOpaqueFullCube(world, pathPos)) {
                    world.setBlockState(pathPos, random.nextInt(3) == 0 ?
                            Blocks.COBBLESTONE.getDefaultState() :
                            Blocks.STONE.getDefaultState(), 3);
                }
            }
        }

        return true;
    }

    private boolean generateStoneLantern(StructureWorldAccess world, BlockPos pos, Random random) {
        // Traditional ishidoro stone lantern
        BlockState stone = Blocks.STONE.getDefaultState();
        BlockState stoneBrick = Blocks.STONE_BRICKS.getDefaultState();
        BlockState chiseledStone = Blocks.CHISELED_STONE_BRICKS.getDefaultState();

        // Base platform
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(pos.add(x, 0, z), stone, 3);
            }
        }

        // Pillar
        world.setBlockState(pos.up(1), stoneBrick, 3);
        world.setBlockState(pos.up(2), chiseledStone, 3);

        // Light chamber (with actual light)
        world.setBlockState(pos.up(3), Blocks.SHROOMLIGHT.getDefaultState(), 3);

        // Roof cap
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(pos.add(x, 4, z), Blocks.STONE_BRICK_SLAB.getDefaultState(), 3);
            }
        }
        world.setBlockState(pos.up(5), Blocks.STONE_BRICK_WALL.getDefaultState(), 3);

        // Moss on base
        if (random.nextInt(2) == 0) {
            world.setBlockState(pos.add(1, 0, 0), Blocks.MOSSY_STONE_BRICKS.getDefaultState(), 3);
        }

        return true;
    }

    private boolean generateShrine(StructureWorldAccess world, BlockPos origin, Random random) {
        BlockState oakPlanks = Blocks.OAK_PLANKS.getDefaultState();
        BlockState darkOakPlanks = Blocks.DARK_OAK_PLANKS.getDefaultState();
        BlockState darkOakLog = Blocks.DARK_OAK_LOG.getDefaultState();
        BlockState redConcrete = Blocks.RED_CONCRETE.getDefaultState();

        int width = 5;
        int depth = 4;
        int height = 4;

        // Foundation (raised platform)
        for (int x = -width / 2 - 1; x <= width / 2 + 1; x++) {
            for (int z = -1; z <= depth + 1; z++) {
                world.setBlockState(origin.add(x, 0, z), Blocks.STONE_BRICKS.getDefaultState(), 3);
            }
        }

        // Floor
        for (int x = -width / 2; x <= width / 2; x++) {
            for (int z = 0; z <= depth; z++) {
                world.setBlockState(origin.add(x, 1, z), oakPlanks, 3);
            }
        }

        // Pillars at corners
        for (int y = 1; y <= height; y++) {
            world.setBlockState(origin.add(-width / 2, y, 0), darkOakLog, 3);
            world.setBlockState(origin.add(width / 2, y, 0), darkOakLog, 3);
            world.setBlockState(origin.add(-width / 2, y, depth), darkOakLog, 3);
            world.setBlockState(origin.add(width / 2, y, depth), darkOakLog, 3);
        }

        // Walls (back and sides, front is open)
        for (int x = -width / 2; x <= width / 2; x++) {
            for (int y = 2; y <= height; y++) {
                world.setBlockState(origin.add(x, y, depth), darkOakPlanks, 3);
            }
        }
        for (int z = 1; z <= depth; z++) {
            for (int y = 2; y <= height; y++) {
                world.setBlockState(origin.add(-width / 2, y, z), oakPlanks, 3);
                world.setBlockState(origin.add(width / 2, y, z), oakPlanks, 3);
            }
        }

        // Roof (overhanging dark oak stairs)
        for (int x = -width / 2 - 1; x <= width / 2 + 1; x++) {
            for (int z = -1; z <= depth + 1; z++) {
                world.setBlockState(origin.add(x, height + 1, z), redConcrete, 3);
            }
        }
        // Peaked roof center
        for (int x = -width / 2; x <= width / 2; x++) {
            world.setBlockState(origin.add(x, height + 2, depth / 2), redConcrete, 3);
        }

        // Offering box inside
        world.setBlockState(origin.add(0, 2, depth - 1), Blocks.CHEST.getDefaultState(), 3);

        // Shimenawa rope (wool across entrance)
        for (int x = -width / 2; x <= width / 2; x++) {
            world.setBlockState(origin.add(x, height, 0), Blocks.WHITE_WOOL.getDefaultState(), 3);
        }

        // Bell
        world.setBlockState(origin.add(0, height - 1, 0), Blocks.BELL.getDefaultState(), 3);

        // Entrance steps
        for (int x = -1; x <= 1; x++) {
            world.setBlockState(origin.add(x, 0, -1), Blocks.STONE_BRICK_STAIRS.getDefaultState(), 3);
        }

        // Stone lanterns flanking entrance
        generateStoneLantern(world, origin.add(-width / 2 - 2, 0, -1), random);
        generateStoneLantern(world, origin.add(width / 2 + 2, 0, -1), random);

        return true;
    }

    private boolean generateWoodenBridge(StructureWorldAccess world, BlockPos origin, Random random) {
        int bridgeLength = 6 + random.nextInt(5);
        BlockState oakPlanks = Blocks.OAK_PLANKS.getDefaultState();
        BlockState fence = Blocks.OAK_FENCE.getDefaultState();

        // Arched bridge
        for (int z = 0; z < bridgeLength; z++) {
            // Arch height (parabolic)
            double t = (double) z / (bridgeLength - 1) - 0.5;
            int archY = (int) (-4 * t * t * 3 + 2); // Gentle arch

            for (int x = -1; x <= 1; x++) {
                BlockPos plankPos = origin.add(x, archY, z);
                world.setBlockState(plankPos, oakPlanks, 3);

                // Support pillars underneath
                for (int y = archY - 1; y >= -3; y--) {
                    BlockPos supportPos = origin.add(x, y, z);
                    if (world.getBlockState(supportPos).isAir() ||
                            world.getBlockState(supportPos).isOf(Blocks.WATER)) {
                        world.setBlockState(supportPos, Blocks.OAK_LOG.getDefaultState(), 3);
                    } else {
                        break;
                    }
                }
            }

            // Railings
            world.setBlockState(origin.add(-2, archY + 1, z), fence, 3);
            world.setBlockState(origin.add(2, archY + 1, z), fence, 3);
        }

        return true;
    }

    private boolean generateOnsen(StructureWorldAccess world, BlockPos origin, Random random) {
        int poolRadius = 3 + random.nextInt(2);

        // Dig the pool
        for (int x = -poolRadius; x <= poolRadius; x++) {
            for (int z = -poolRadius; z <= poolRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist <= poolRadius + (random.nextFloat() - 0.5)) {
                    // Dig down
                    int depth = dist < poolRadius - 1 ? 2 : 1;
                    for (int y = 0; y >= -depth; y--) {
                        BlockPos poolPos = origin.add(x, y, z);
                        if (y == -depth) {
                            // Bottom: magma blocks for heat
                            if (random.nextInt(3) == 0) {
                                world.setBlockState(poolPos, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
                            } else {
                                world.setBlockState(poolPos, Blocks.SMOOTH_STONE.getDefaultState(), 3);
                            }
                        } else {
                            // Fill with water
                            world.setBlockState(poolPos, Blocks.WATER.getDefaultState(), 3);
                        }
                    }
                }
            }
        }

        // Stone rim
        for (int x = -poolRadius - 1; x <= poolRadius + 1; x++) {
            for (int z = -poolRadius - 1; z <= poolRadius + 1; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > poolRadius - 0.5 && dist <= poolRadius + 1.5) {
                    BlockPos rimPos = origin.add(x, 0, z);
                    if (!world.getBlockState(rimPos).isOf(Blocks.WATER)) {
                        BlockState rimBlock = random.nextInt(3) == 0 ?
                                Blocks.MOSSY_COBBLESTONE.getDefaultState() :
                                Blocks.COBBLESTONE.getDefaultState();
                        world.setBlockState(rimPos, rimBlock, 3);
                    }
                }
            }
        }

        // Bamboo screen on one side
        int screenDir = random.nextInt(4);
        for (int i = -2; i <= 2; i++) {
            int sx = screenDir < 2 ? i : (screenDir == 2 ? poolRadius + 2 : -poolRadius - 2);
            int sz = screenDir >= 2 ? i : (screenDir == 0 ? poolRadius + 2 : -poolRadius - 2);
            for (int y = 1; y <= 3; y++) {
                world.setBlockState(origin.add(sx, y, sz), Blocks.BAMBOO_PLANKS.getDefaultState(), 3);
            }
        }

        // Nearby rocks (decorative boulders)
        for (int i = 0; i < 3; i++) {
            double angle = random.nextFloat() * Math.PI * 2;
            int rx = (int) (Math.cos(angle) * (poolRadius + 2));
            int rz = (int) (Math.sin(angle) * (poolRadius + 2));
            world.setBlockState(origin.add(rx, 0, rz), Blocks.COBBLESTONE.getDefaultState(), 3);
            if (random.nextInt(2) == 0) {
                world.setBlockState(origin.add(rx, 1, rz), Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3);
            }
        }

        return true;
    }

    private boolean generateZenGarden(StructureWorldAccess world, BlockPos origin, Random random) {
        int gardenRadius = 4 + random.nextInt(3);

        // Sand base (raked zen garden)
        for (int x = -gardenRadius; x <= gardenRadius; x++) {
            for (int z = -gardenRadius; z <= gardenRadius; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist <= gardenRadius) {
                    BlockPos gardenPos = origin.add(x, -1, z);
                    world.setBlockState(gardenPos, Blocks.SAND.getDefaultState(), 3);
                    // Clear above
                    world.setBlockState(origin.add(x, 0, z), Blocks.AIR.getDefaultState(), 3);
                }
            }
        }

        // Decorative rocks (3-5)
        int rockCount = 3 + random.nextInt(3);
        for (int i = 0; i < rockCount; i++) {
            double angle = (Math.PI * 2 * i) / rockCount + random.nextGaussian() * 0.3;
            double dist = (gardenRadius * 0.3) + random.nextFloat() * (gardenRadius * 0.4);
            int rx = (int) (Math.cos(angle) * dist);
            int rz = (int) (Math.sin(angle) * dist);

            BlockState rock = random.nextInt(2) == 0 ?
                    Blocks.STONE.getDefaultState() :
                    Blocks.ANDESITE.getDefaultState();
            world.setBlockState(origin.add(rx, 0, rz), rock, 3);
            if (random.nextInt(2) == 0) {
                world.setBlockState(origin.add(rx, 1, rz), rock, 3);
            }
        }

        // Surrounding stone border
        for (int x = -gardenRadius - 1; x <= gardenRadius + 1; x++) {
            for (int z = -gardenRadius - 1; z <= gardenRadius + 1; z++) {
                double dist = Math.sqrt(x * x + z * z);
                if (dist > gardenRadius - 0.5 && dist <= gardenRadius + 1) {
                    world.setBlockState(origin.add(x, -1, z), Blocks.STONE_BRICKS.getDefaultState(), 3);
                }
            }
        }

        // Small Japanese maple or moss patch in corner
        if (random.nextInt(2) == 0) {
            int mx = gardenRadius - 1;
            int mz = gardenRadius - 1;
            world.setBlockState(origin.add(mx, 0, mz), Blocks.MOSS_BLOCK.getDefaultState(), 3);
            world.setBlockState(origin.add(mx, 1, mz), Blocks.FLOWERING_AZALEA.getDefaultState(), 3);
        }

        return true;
    }
}
