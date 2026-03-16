package dev.cxd.dominium.block;

import dev.cxd.dominium.block.entity.ObeliskBlockEntity;
import dev.cxd.dominium.init.ModBlockEntities;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ObeliskBlock extends BlockWithEntity {

    public enum ObeliskPart implements StringIdentifiable {
        LOWER, MIDDLE, UPPER;

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }

    public static final EnumProperty<ObeliskPart> PART = EnumProperty.of("part", ObeliskPart.class);

    private static final VoxelShape LOWER_SHAPE  = VoxelShapes.fullCube();
    private static final VoxelShape MIDDLE_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape UPPER_SHAPE  = VoxelShapes.fullCube();

    public ObeliskBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(PART, ObeliskPart.LOWER));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;

        BlockPos lowerPos = getLowerPos(pos, state);
        if (!(world.getBlockEntity(lowerPos) instanceof ObeliskBlockEntity be)) return ActionResult.PASS;

        if (player.isSneaking() && be.isOwner(player.getUuid())) {
            boolean newState = !be.isNoEnchantZoneEnabled();
            be.setNoEnchantZoneEnabled(newState, world);
            player.sendMessage(Text.literal("No-enchant zone " + (newState ? "§aenabled" : "§cdisabled")), true);
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 4F, 0.5F);
            return ActionResult.SUCCESS;
        }

        if (be.isAllowed(player.getUuid()) && player instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(lowerPos);
            buf.writeInt(be.OBELISK_PROTECTION_RADIUS);
            buf.writeInt(be.OBELISK_PROTECTION_HEIGHT);
            ServerPlayNetworking.send(serverPlayer, ModPackets.SHOW_CLAIM_RADIUS_ID, buf);
            player.sendMessage(Text.literal("§3Claim radius: §b" +
                    be.OBELISK_PROTECTION_RADIUS + " blocks"), true);
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(PART) != ObeliskPart.LOWER) return true;
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState base = Objects.requireNonNull(super.getPlacementState(ctx));
        BlockPos pos = ctx.getBlockPos();

        if (!ctx.getWorld().getBlockState(pos.up()).isAir()
                || !ctx.getWorld().getBlockState(pos.up(2)).isAir()) {
            return null;
        }

        return base.with(PART, ObeliskPart.LOWER);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClient()) return;

        world.setBlockState(pos.up(),  state.with(PART, ObeliskPart.MIDDLE), Block.NOTIFY_ALL);
        world.setBlockState(pos.up(2), state.with(PART, ObeliskPart.UPPER),  Block.NOTIFY_ALL);

        if (placer != null && world.getBlockEntity(pos) instanceof ObeliskBlockEntity be) {
            be.setOwner(placer.getUuid());
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockPos lowerPos = getLowerPos(pos, state);

            if (world.getBlockEntity(lowerPos) instanceof ObeliskBlockEntity be) {
                if (!be.isAllowed(player.getUuid())) {
                    switch (state.get(PART)) {
                        case LOWER -> {
                            if (world.getBlockEntity(pos) instanceof ObeliskBlockEntity lowerBe) {
                                lowerBe.setNoEnchantZoneEnabled(false, world);
                            }
                            clearPart(world, pos.up());
                            clearPart(world, pos.up(2));
                        }
                        case MIDDLE -> { clearPart(world, pos.down()); clearPart(world, pos.up()); }
                        case UPPER  -> { clearPart(world, pos.down()); clearPart(world, pos.down(2)); }
                    }
                    Block.dropStack(world, pos, new ItemStack(ModItems.EYE_OF_THE_APEX));
                    super.onBreak(world, pos, state, player);
                    return;
                }
            }

            switch (state.get(PART)) {
                case LOWER -> {
                    if (world.getBlockEntity(pos) instanceof ObeliskBlockEntity be2) {
                        be2.setNoEnchantZoneEnabled(false, world);
                    }
                    clearPart(world, pos.up());
                    clearPart(world, pos.up(2));
                }
                case MIDDLE -> { clearPart(world, pos.down()); clearPart(world, pos.up()); }
                case UPPER  -> { clearPart(world, pos.down()); clearPart(world, pos.down(2)); }
            }
            Block.dropStack(world, pos, new ItemStack(ModItems.EYE_OF_THE_APEX));
        }
        super.onBreak(world, pos, state, player);
    }

    private BlockPos getLowerPos(BlockPos pos, BlockState state) {
        return switch (state.get(PART)) {
            case LOWER  -> pos;
            case MIDDLE -> pos.down();
            case UPPER  -> pos.down(2);
        };
    }

    private void clearPart(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof ObeliskBlock) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(PART)) {
            case LOWER  -> LOWER_SHAPE;
            case MIDDLE -> MIDDLE_SHAPE;
            case UPPER  -> UPPER_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(PART) == ObeliskPart.LOWER) {
            return new ObeliskBlockEntity(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient() || state.get(PART) != ObeliskPart.LOWER) return null;
        return type == ModBlockEntities.OBELISK_BLOCK_ENTITY
                ? (BlockEntityTicker<T>) (BlockEntityTicker<ObeliskBlockEntity>) ObeliskBlockEntity::tick
                : null;
    }
}