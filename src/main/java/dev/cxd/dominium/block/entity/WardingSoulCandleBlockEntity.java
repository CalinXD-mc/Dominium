package dev.cxd.dominium.block.entity;

import dev.cxd.dominium.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class WardingSoulCandleBlockEntity extends BlockEntity {
    public WardingSoulCandleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUL_CANDLE_BLOCK_ENTITY, pos, state);
    }
}
