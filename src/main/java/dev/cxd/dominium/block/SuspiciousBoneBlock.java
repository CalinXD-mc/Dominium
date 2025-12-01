package dev.cxd.dominium.block;

import dev.cxd.dominium.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SuspiciousBoneBlock extends FallingBlock {
    private boolean alternateName = false;

    public SuspiciousBoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, this.getFallDelay());
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 20); // schedule first tick in 20 ticks (1 second)
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {

        BlockPos belowPos = pos.down();
        BlockState blockBelow = world.getBlockState(belowPos);

        if (world.getRegistryKey() == World.NETHER) {

            if (blockBelow.getBlock() == Blocks.LAVA
                    && world.getBlockState(belowPos.down()).getBlock() == Blocks.MAGMA_BLOCK) {

                world.setBlockState(belowPos, Blocks.AIR.getDefaultState());

                boolean massiveExplosion = random.nextFloat() < 0.05F;
                float explosionPower = massiveExplosion ? 20.0F : 2.0F;

                boolean diamondBlock = random.nextFloat() < 0.05F;

                world.createExplosion(
                        null,
                        null,
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        explosionPower,
                        true,
                        World.ExplosionSourceType.BLOCK
                );

                world.setBlockState(pos, diamondBlock ? Blocks.ANCIENT_DEBRIS.getDefaultState()
                        : ModBlocks.ANCIENTER_DEBRIS.getDefaultState());

                return;
            }
        }

        if (canFallThrough(blockBelow) && pos.getY() >= world.getBottomY()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            this.configureFallingBlockEntity(fallingBlockEntity);
        }

        alternateName = !alternateName;

        world.scheduleBlockTick(pos, this, 20);
    }


    @Override
    public MutableText getName() {
        if (alternateName) {
            return Text.translatable("block.dominium.suspicious_bone_block.alt");
        } else {
            return Text.translatable("block.dominium.suspicious_bone_block");
        }
    }
}