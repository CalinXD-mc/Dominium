package dev.cxd.dominium.block;

import net.minecraft.block.GlassBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

import java.util.HashSet;
import java.util.Set;

public class SoulGlass extends GlassBlock {

    public static final BooleanProperty ON = BooleanProperty.of("on");
    private static final int MAX_PROPAGATION = 127;

    public SoulGlass(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            world.setBlockState(pos, state.cycle(ON), 3);
            schedulePropagation(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    private void schedulePropagation(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this) && neighborState.get(ON) != world.getBlockState(pos).get(ON)) {
                world.scheduleBlockTick(neighborPos, this, 1); // schedule tick in 4 ticks (~0.2s)
            }
        }
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return state.get(ON) ? 15 : 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean newState = !state.get(ON);
        world.setBlockState(pos, state.with(ON, newState), 3);
        schedulePropagation(world, pos);
        super.scheduledTick(state, world, pos, random);
    }
}