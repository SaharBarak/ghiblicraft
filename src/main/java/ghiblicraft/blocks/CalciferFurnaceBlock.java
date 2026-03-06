package ghiblicraft.blocks;

import ghiblicraft.blocks.entity.CalciferFurnaceBlockEntity;
import ghiblicraft.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CalciferFurnaceBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    public CalciferFurnaceBlock() {
        super(Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .requiresTool()
                .strength(3.5f)
                .luminance(state -> state.get(LIT) ? 13 : 0));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LIT, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CalciferFurnaceBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                    BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CALCIFER_FURNACE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.3;
            double z = pos.getZ() + 0.5;

            // Calcifer's face - orange flame with personality
            if (random.nextInt(3) == 0) {
                world.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                        SoundCategory.BLOCKS, 0.5f, 1.0f + random.nextFloat() * 0.5f, false);
            }

            // Fiery eyes
            world.addParticle(ParticleTypes.FLAME,
                    x - 0.15, y + 0.4, z + 0.45, 0, 0.02, 0);
            world.addParticle(ParticleTypes.FLAME,
                    x + 0.15, y + 0.4, z + 0.45, 0, 0.02, 0);

            // Body flames
            for (int i = 0; i < 3; i++) {
                world.addParticle(ParticleTypes.FLAME,
                        x + (random.nextFloat() - 0.5) * 0.4,
                        y + random.nextFloat() * 0.3,
                        z + (random.nextFloat() - 0.5) * 0.4,
                        0, 0.03 + random.nextFloat() * 0.02, 0);
            }

            // Occasional soul-like spark
            if (random.nextInt(10) == 0) {
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        x, y + 0.6, z, 0, 0.05, 0);
            }
        }
    }
}
