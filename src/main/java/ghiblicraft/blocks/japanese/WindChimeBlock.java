package ghiblicraft.blocks.japanese;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Japanese Wind Chime (Furin).
 * Plays gentle chiming sounds when there's wind (random tick).
 * Hangs from ceiling. Creates a peaceful atmosphere.
 */
public class WindChimeBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(5, 2, 5, 11, 16, 11);

    public WindChimeBlock() {
        super(Settings.create()
                .mapColor(MapColor.CLEAR)
                .strength(0.2f)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.AMETHYST_BLOCK));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.world.ServerWorld world,
                           BlockPos pos, Random random) {
        // Chime sound
        float[] notes = {1.0f, 1.189f, 1.335f, 1.498f, 1.682f};
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.BLOCKS, 0.4f, notes[random.nextInt(notes.length)]);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0) {
            world.addParticle(ParticleTypes.NOTE,
                    pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                    random.nextFloat(), 0, 0);
        }
    }
}
