package ghiblicraft.blocks.japanese;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class TatamiBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public TatamiBlock() {
        super(Settings.create()
                .mapColor(MapColor.PALE_YELLOW)
                .strength(0.5f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
