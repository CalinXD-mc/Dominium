package dev.cxd.dominium.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public interface IChunkBlockCacheComponent extends ComponentV3 {
    void addToChunk(ChunkPos chunkPos, Block block, BlockPos pos);

    List<BlockPos> getBlocksFromChunk(ChunkPos chunkPos, Block block);

    void removeFromChunk(ChunkPos chunkPos, Block block, BlockPos pos);
}
