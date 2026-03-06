package ghiblicraft.blocks;

import ghiblicraft.registry.ModParticles;
import ghiblicraft.registry.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class MusicBoxBlock extends Block {
    public static final BooleanProperty PLAYING = BooleanProperty.of("playing");

    public MusicBoxBlock() {
        super(Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .strength(1.5f)
                .luminance(state -> state.get(PLAYING) ? 7 : 0));
        this.setDefaultState(this.stateManager.getDefaultState().with(PLAYING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLAYING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        boolean isPlaying = !state.get(PLAYING);
        world.setBlockState(pos, state.with(PLAYING, isPlaying), 3);

        if (isPlaying) {
            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(),
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(PLAYING);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(PLAYING)) return;

        // Play gentle melody notes
        float[] notes = {1.0f, 1.122f, 1.26f, 1.414f, 1.587f, 1.682f, 1.888f};
        float note = notes[random.nextInt(notes.length)];
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(),
                SoundCategory.BLOCKS, 0.5f, note);

        // Grow flowers nearby
        growFlowersNearby(world, pos, random);

        // Attract nearby spirits
        attractSpirits(world, pos);

        // Spawn enchanting particles
        for (int i = 0; i < 5; i++) {
            world.spawnParticles(ParticleTypes.NOTE,
                    pos.getX() + 0.5 + random.nextGaussian() * 0.5,
                    pos.getY() + 1.0 + random.nextFloat(),
                    pos.getZ() + 0.5 + random.nextGaussian() * 0.5,
                    1, 0, 0, 0, 0);
        }
    }

    private void growFlowersNearby(ServerWorld world, BlockPos pos, Random random) {
        int radius = 6;
        for (int attempt = 0; attempt < 3; attempt++) {
            int x = pos.getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = pos.getZ() + random.nextInt(radius * 2 + 1) - radius;

            for (int y = pos.getY() - 3; y <= pos.getY() + 3; y++) {
                BlockPos checkPos = new BlockPos(x, y, z);
                BlockPos above = checkPos.up();

                if (world.getBlockState(checkPos).isOf(Blocks.GRASS_BLOCK) &&
                        world.getBlockState(above).isAir()) {
                    Block[] flowers = {
                            Blocks.DANDELION, Blocks.POPPY, Blocks.CORNFLOWER,
                            Blocks.LILY_OF_THE_VALLEY, Blocks.AZURE_BLUET,
                            Blocks.OXEYE_DAISY, Blocks.ALLIUM
                    };
                    world.setBlockState(above, flowers[random.nextInt(flowers.length)].getDefaultState(), 3);
                    break;
                }
            }
        }
    }

    private void attractSpirits(ServerWorld world, BlockPos pos) {
        List<PathAwareEntity> spirits = world.getEntitiesByClass(
                PathAwareEntity.class,
                new Box(pos).expand(24),
                entity -> entity.getType().getRegistryEntry().registryKey().getValue().getNamespace().equals("ghiblicraft"));

        for (PathAwareEntity spirit : spirits) {
            double dx = spirit.getX() - (pos.getX() + 0.5);
            double dz = spirit.getZ() - (pos.getZ() + 0.5);
            if (Math.sqrt(dx * dx + dz * dz) > 5) {
                spirit.getNavigation().startMovingTo(
                        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.5);
            }
        }
    }
}
