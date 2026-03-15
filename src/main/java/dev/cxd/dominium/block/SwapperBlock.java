package dev.cxd.dominium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.UUID;

public class SwapperBlock extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public SwapperBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(AXIS, Direction.Axis.Y)
                .with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(AXIS, ctx.getSide().getAxis())
                .with(POWERED, false);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        boolean isPowered = world.isReceivingRedstonePower(pos);
        boolean wasPowered = state.get(POWERED);

        if (isPowered && !wasPowered) {
            world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL);
            Direction facing = switch (state.get(AXIS)) {
                case X -> Direction.EAST;
                case Y -> Direction.UP;
                case Z -> Direction.NORTH;
            };
            swapBlocks(world, pos, facing);
        } else if (!isPowered && wasPowered) {
            world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL);
        }
    }

    private void swapBlocks(World world, BlockPos swapperPos, Direction facing) {
        BlockPos posA = swapperPos.offset(facing);
        BlockPos posB = swapperPos.offset(facing.getOpposite());

        BlockState stateA = world.getBlockState(posA);
        BlockState stateB = world.getBlockState(posB);

        if (stateA.isAir() && stateB.isAir()) return;
        if (stateA.getBlock() instanceof SwapperBlock || stateB.getBlock() instanceof SwapperBlock) return;

        UUID signerA = getVesselSigner(world, posA);
        UUID signerB = getVesselSigner(world, posB);

        NbtCompound nbtA = captureBlockEntityNbt(world, posA);
        NbtCompound nbtB = captureBlockEntityNbt(world, posB);

        if (world instanceof ServerWorld serverWorld) {
            removeBlockEntitySafely(serverWorld, posA);
            removeBlockEntitySafely(serverWorld, posB);
        }

        BlockState cleanA = stripRedstoneState(stateA);
        BlockState cleanB = stripRedstoneState(stateB);

        int flags = Block.NOTIFY_ALL | Block.FORCE_STATE;
        world.setBlockState(posA, cleanB, flags);
        world.setBlockState(posB, cleanA, flags);

        if (nbtA != null) restoreBlockEntityNbt(world, posB, nbtA);
        if (nbtB != null) restoreBlockEntityNbt(world, posA, nbtB);

        world.updateNeighbors(posA, cleanB.getBlock());
        world.updateNeighbors(posB, cleanA.getBlock());

        cleanB.neighborUpdate(world, posA, cleanB.getBlock(), swapperPos, false);
        cleanA.neighborUpdate(world, posB, cleanA.getBlock(), swapperPos, false);

        if (signerA != null && signerB != null && world instanceof ServerWorld serverWorld) {
            ServerPlayerEntity playerA = serverWorld.getServer().getPlayerManager().getPlayer(signerA);
            ServerPlayerEntity playerB = serverWorld.getServer().getPlayerManager().getPlayer(signerB);

            if (playerA != null && playerB != null) {
                double ax = playerA.getX(), ay = playerA.getY(), az = playerA.getZ();
                double bx = playerB.getX(), by = playerB.getY(), bz = playerB.getZ();
                float aYaw = playerA.getYaw(), aPitch = playerA.getPitch();
                float bYaw = playerB.getYaw(), bPitch = playerB.getPitch();

                playerA.teleport(serverWorld, bx, by, bz, bYaw, bPitch);
                playerB.teleport(serverWorld, ax, ay, az, aYaw, aPitch);
            }
        }
    }

    private UUID getVesselSigner(World world, BlockPos pos) {
        if (!(world.getBlockState(pos).getBlock() instanceof VesselBlock)) return null;
        if (!(world.getBlockEntity(pos) instanceof dev.cxd.dominium.block.entity.VesselBlockEntity be)) return null;
        return be.getSignerUUID();
    }

    private BlockState stripRedstoneState(BlockState state) {
        BlockState stripped = state;

        if (stripped.contains(Properties.POWERED)) {
            stripped = stripped.with(Properties.POWERED, false);
        }
        if (stripped.contains(Properties.LIT)) {
            stripped = stripped.with(Properties.LIT, false);
        }
        if (stripped.contains(Properties.POWER)) {
            stripped = stripped.with(Properties.POWER, 0);
        }
        if (stripped.contains(Properties.ENABLED)) {
            stripped = stripped.with(Properties.ENABLED, true);
        }

        return stripped;
    }

    private NbtCompound captureBlockEntityNbt(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return null;
        return be.createNbt();
    }

    private void removeBlockEntitySafely(ServerWorld world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null) {
            world.removeBlockEntity(pos);
        }
    }

    private void restoreBlockEntityNbt(World world, BlockPos pos, NbtCompound nbt) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return;

        NbtCompound adjusted = nbt.copy();
        adjusted.putInt("x", pos.getX());
        adjusted.putInt("y", pos.getY());
        adjusted.putInt("z", pos.getZ());
        be.readNbt(adjusted);
        be.markDirty();
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }
}