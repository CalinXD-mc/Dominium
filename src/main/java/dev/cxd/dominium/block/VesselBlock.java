package dev.cxd.dominium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class VesselBlock extends Block {
    private static final BooleanProperty LIT;
    public static final DirectionProperty FACING;
    public static final VoxelShape BASE_SHAPE;

    public VesselBlock(Settings settings) {
        super(settings.luminance(state -> state.get(LIT) ? 3 : 0));
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return BASE_SHAPE;
    }

    static {
        LIT = RedstoneTorchBlock.LIT;
        BASE_SHAPE = Block.createCuboidShape(6.0F, 0.0F, 6.0F, 10.0F, 12.0F, 10.0F);
        FACING = Properties.HORIZONTAL_FACING;
    }
}
