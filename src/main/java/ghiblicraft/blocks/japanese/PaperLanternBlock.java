package ghiblicraft.blocks.japanese;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Japanese Paper Lantern (Chochin).
 * Warm glowing light source. Can hang from ceilings or sit on surfaces.
 * Emits gentle flame particles.
 */
public class PaperLanternBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 14, 12);

    public PaperLanternBlock() {
        super(Settings.create()
                .mapColor(MapColor.ORANGE)
                .strength(0.3f)
                .luminance(state -> 14)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.7;
        double z = pos.getZ() + 0.5;

        // Warm glow particles
        if (random.nextInt(3) == 0) {
            world.addParticle(ParticleTypes.FLAME,
                    x + (random.nextFloat() - 0.5) * 0.2,
                    y,
                    z + (random.nextFloat() - 0.5) * 0.2,
                    0, 0.005, 0);
        }
    }
}
