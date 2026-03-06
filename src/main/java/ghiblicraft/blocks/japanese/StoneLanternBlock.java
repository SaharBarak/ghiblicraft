package ghiblicraft.blocks.japanese;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Stone Garden Lantern (Ishidoro).
 * Traditional stone lantern found in Japanese gardens and temple paths.
 */
public class StoneLanternBlock extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4, 0, 4, 12, 3, 12),   // Base
            Block.createCuboidShape(6, 3, 6, 10, 8, 10),    // Pillar
            Block.createCuboidShape(3, 8, 3, 13, 12, 13),   // Chamber
            Block.createCuboidShape(2, 12, 2, 14, 14, 14)   // Roof
    );

    public StoneLanternBlock() {
        super(Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .strength(2.0f)
                .luminance(state -> 10)
                .nonOpaque()
                .requiresTool());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(4) == 0) {
            world.addParticle(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                    0, 0.01, 0);
        }
    }
}
