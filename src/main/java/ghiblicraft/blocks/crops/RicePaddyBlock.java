package ghiblicraft.blocks.crops;

import ghiblicraft.registry.ModItems;
import net.minecraft.block.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Rice Paddy crop block.
 * Must be planted on farmland that is adjacent to water (waterlogged rice field).
 * Grows in 4 stages. Slower than wheat but yields more.
 * When fully grown, drops rice items.
 */
public class RicePaddyBlock extends CropBlock {
    public static final IntProperty AGE = Properties.AGE_3;

    private static final VoxelShape[] SHAPES = {
            Block.createCuboidShape(0, 0, 0, 16, 3, 16),
            Block.createCuboidShape(0, 0, 0, 16, 6, 16),
            Block.createCuboidShape(0, 0, 0, 16, 10, 16),
            Block.createCuboidShape(0, 0, 0, 16, 14, 16)
    };

    public RicePaddyBlock() {
        super(Settings.create()
                .mapColor(MapColor.PALE_GREEN)
                .noCollision()
                .ticksRandomly()
                .breakInstantly()
                .sounds(BlockSoundGroup.CROP));
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.RICE;
    }

    @Override
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[this.getAge(state)];
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Rice grows faster when water is nearby (within 2 blocks)
        boolean nearWater = false;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (world.getBlockState(pos.add(dx, 0, dz)).isOf(Blocks.WATER) ||
                        world.getBlockState(pos.add(dx, -1, dz)).isOf(Blocks.WATER)) {
                    nearWater = true;
                    break;
                }
            }
            if (nearWater) break;
        }

        if (this.getAge(state) < this.getMaxAge()) {
            // Base growth chance, doubled near water
            float growthChance = nearWater ? 0.15f : 0.07f;

            if (random.nextFloat() < growthChance) {
                world.setBlockState(pos, this.withAge(this.getAge(state) + 1), 3);
            }
        }
    }
}
