package dev.cxd.dominium.block;

import dev.cxd.dominium.block.entity.VesselBlockEntity;
import dev.cxd.dominium.init.ModBlockEntities;
import dev.cxd.dominium.init.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VesselBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(6.0F, 0.0F, 6.0F, 10.0F, 12.0F, 10.0F);

    public VesselBlock(Settings settings) {
        super(settings.luminance(state -> state.get(LIT) ? 3 : 0));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT, false)
                .with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(LIT, shouldBeLit(ctx.getWorld(), ctx.getBlockPos()));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof VesselBlockEntity be)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);

        if (be.hasContract()) {
            ItemStack contract = be.removeContract();
            player.giveItemStack(contract);
            world.setBlockState(pos, state.with(LIT, false), Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        if (!held.isEmpty() && held.isOf(ModItems.CONTRACT_SIGNED) && !be.hasContract()) {
            be.insertContract(held);
            if (!player.isCreative()) held.decrement(1);
            world.setBlockState(pos, state.with(LIT, shouldBeLit(world, pos)), Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof VesselBlockEntity be) {
            if (be.hasContract()) {
                Block.dropStack(world, pos, be.getContract());
            }
        }
        super.onBreak(world, pos, state, player);
    }

    private boolean shouldBeLit(World world, BlockPos pos) {
        if (!isSoulFireBelow(world, pos)) return false;
        if (world.getBlockEntity(pos) instanceof VesselBlockEntity be) {
            return be.hasContract();
        }
        return false;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) return;
        boolean lit = shouldBeLit(world, pos);
        if (state.get(LIT) != lit) {
            world.setBlockState(pos, state.with(LIT, lit), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos,
                             BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            boolean lit = shouldBeLit(world, pos);
            if (state.get(LIT) != lit) {
                world.setBlockState(pos, state.with(LIT, lit), Block.NOTIFY_ALL);
            }
        }
    }

    private boolean isSoulFireBelow(World world, BlockPos pos) {
        BlockPos below = pos.down(2);
        Block block = world.getBlockState(below).getBlock();
        return block == Blocks.SOUL_FIRE
                || block == Blocks.SOUL_TORCH
                || block == Blocks.SOUL_WALL_TORCH
                || block == Blocks.SOUL_LANTERN
                || block == Blocks.SOUL_CAMPFIRE;
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VesselBlockEntity(pos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return type == ModBlockEntities.VESSEL_BLOCK_ENTITY
                ? (BlockEntityTicker<T>) (BlockEntityTicker<VesselBlockEntity>) VesselBlockEntity::tick
                : null;
    }
}