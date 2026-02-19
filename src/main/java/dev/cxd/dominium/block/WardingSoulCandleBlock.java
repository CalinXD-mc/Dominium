package dev.cxd.dominium.block;

import dev.cxd.dominium.block.entity.WardingSoulCandleBlockEntity;
import dev.cxd.dominium.init.ModComponents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WardingSoulCandleBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final VoxelShape BASE_SHAPE;

    public WardingSoulCandleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WardingSoulCandleBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 17.5 / 16.0;
        double z = pos.getZ() + 0.5;

        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        ModComponents.getChunkBlockCache(world).ifPresent(cache ->
                cache.addToChunk(new ChunkPos(pos), this, pos)
        );
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        ModComponents.getChunkBlockCache(world).ifPresent(cache ->
                cache.removeFromChunk(new ChunkPos(pos), this, pos)
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return BASE_SHAPE;
    }

    static {
        BASE_SHAPE = Block.createCuboidShape(6.0F, 0.0F, 6.0F, 10.0F, 14.0F, 10.0F);
    }
}
