package ghiblicraft.worldgen.structures;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Koi Pond — small decorative water feature found in Japanese gardens.
 * Oval pond with stone rim, lily pads, tropical fish, and bamboo fountain.
 */
public class KoiPondFeature extends Feature<DefaultFeatureConfig> {
    public KoiPondFeature(Codec<DefaultFeatureConfig> codec) {
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

        int radiusX = 3 + random.nextInt(2);
        int radiusZ = 2 + random.nextInt(2);

        // Dig and fill pond
        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                double dist = (double) (x * x) / (radiusX * radiusX) +
                        (double) (z * z) / (radiusZ * radiusZ);
                if (dist > 1.0) continue;

                BlockPos pondPos = origin.add(x, -1, z);
                world.setBlockState(pondPos,
                        random.nextInt(5) == 0 ? Blocks.CLAY.getDefaultState() :
                                Blocks.GRAVEL.getDefaultState(), 3);

                // Water level
                world.setBlockState(origin.add(x, 0, z), Blocks.WATER.getDefaultState(), 3);

                // Lily pads on surface
                if (random.nextInt(5) == 0 && dist < 0.7) {
                    world.setBlockState(origin.add(x, 1, z), Blocks.LILY_PAD.getDefaultState(), 3);
                }
            }
        }

        // Stone rim
        for (int x = -radiusX - 1; x <= radiusX + 1; x++) {
            for (int z = -radiusZ - 1; z <= radiusZ + 1; z++) {
                double dist = (double) (x * x) / (radiusX * radiusX) +
                        (double) (z * z) / (radiusZ * radiusZ);
                if (dist > 1.0 && dist <= 1.8) {
                    world.setBlockState(origin.add(x, 0, z),
                            random.nextInt(3) == 0 ?
                                    Blocks.MOSSY_COBBLESTONE.getDefaultState() :
                                    Blocks.COBBLESTONE.getDefaultState(), 3);
                }
            }
        }

        // Spawn koi (tropical fish)
        for (int i = 0; i < 3 + random.nextInt(4); i++) {
            int fx = random.nextInt(radiusX * 2 + 1) - radiusX;
            int fz = random.nextInt(radiusZ * 2 + 1) - radiusZ;
            double dist = (double) (fx * fx) / (radiusX * radiusX) +
                    (double) (fz * fz) / (radiusZ * radiusZ);
            if (dist < 0.8) {
                net.minecraft.entity.passive.TropicalFishEntity fish =
                        EntityType.TROPICAL_FISH.create(world.toServerWorld());
                if (fish != null) {
                    fish.setPosition(origin.getX() + fx + 0.5, origin.getY() + 0.5,
                            origin.getZ() + fz + 0.5);
                    world.spawnEntity(fish);
                }
            }
        }

        // Small bamboo fountain
        int fountainX = radiusX;
        world.setBlockState(origin.add(fountainX, 0, 0), Blocks.BAMBOO_PLANKS.getDefaultState(), 3);
        world.setBlockState(origin.add(fountainX, 1, 0), Blocks.BAMBOO_PLANKS.getDefaultState(), 3);

        // Nearby garden elements
        // Flowers
        for (int i = 0; i < 5; i++) {
            double angle = random.nextFloat() * Math.PI * 2;
            int gx = (int) (Math.cos(angle) * (radiusX + 2));
            int gz = (int) (Math.sin(angle) * (radiusZ + 2));
            BlockPos flowerBase = origin.add(gx, 0, gz);
            if (world.getBlockState(flowerBase).isAir() &&
                    world.getBlockState(flowerBase.down()).isOpaqueFullCube(world, flowerBase.down())) {
                world.setBlockState(flowerBase,
                        random.nextInt(2) == 0 ? Blocks.AZURE_BLUET.getDefaultState() :
                                Blocks.LILY_OF_THE_VALLEY.getDefaultState(), 3);
            }
        }

        return true;
    }
}
