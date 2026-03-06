package ghiblicraft.blocks.japanese;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Shoji Screen - Japanese sliding paper door.
 * Right-click to open/close (slides to the side visually).
 * Semi-transparent, lets light through.
 */
public class ShojiScreenBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;

    private static final VoxelShape NS_SHAPE = Block.createCuboidShape(0, 0, 7, 16, 16, 9);
    private static final VoxelShape EW_SHAPE = Block.createCuboidShape(7, 0, 0, 9, 16, 16);

    public ShojiScreenBlock() {
        super(Settings.create()
                .mapColor(MapColor.OFF_WHITE)
                .strength(0.3f)
                .nonOpaque()
                .luminance(state -> 3)
                .sounds(net.minecraft.sound.BlockSoundGroup.BAMBOO));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(OPEN)) return VoxelShapes.empty();
        Direction facing = state.get(FACING);
        return (facing == Direction.NORTH || facing == Direction.SOUTH) ? NS_SHAPE : EW_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        boolean open = !state.get(OPEN);
        world.setBlockState(pos, state.with(OPEN, open), 3);
        world.playSound(null, pos,
                open ? SoundEvents.BLOCK_BAMBOO_BREAK : SoundEvents.BLOCK_BAMBOO_PLACE,
                SoundCategory.BLOCKS, 0.5f, 1.0f);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
