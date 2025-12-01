package dev.cxd.dominium.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class ChunkBlockCacheComponent implements IChunkBlockCacheComponent, Component {
    private final World world;

    // Map<ChunkPos, Map<Block, Set<BlockPos>>>
    private final Map<ChunkPos, Map<Block, Set<BlockPos>>> map = new HashMap<>();

    public ChunkBlockCacheComponent(World world) {
        this.world = world;
    }

    @Override
    public void addToChunk(ChunkPos chunkPos, Block block, BlockPos pos) {
        Map<Block, Set<BlockPos>> chunk = map.computeIfAbsent(chunkPos, cp -> new HashMap<>());
        Set<BlockPos> blocks = chunk.computeIfAbsent(block, b -> new HashSet<>());
        blocks.add(pos);
    }

    @Override
    public List<BlockPos> getBlocksFromChunk(ChunkPos chunkPos, Block block) {
        Map<Block, Set<BlockPos>> chunk = map.get(chunkPos);
        if (chunk == null) {
            return Collections.emptyList();
        }
        Set<BlockPos> positions = chunk.get(block);
        if (positions == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(positions);
    }

    @Override
    public void removeFromChunk(ChunkPos chunkPos, Block block, BlockPos pos) {
        Map<Block, Set<BlockPos>> chunk = map.get(chunkPos);
        if (chunk != null) {
            Set<BlockPos> positions = chunk.get(block);
            if (positions != null) {
                positions.remove(pos);
                if (positions.isEmpty()) {
                    chunk.remove(block);
                }
            }
            if (chunk.isEmpty()) {
                map.remove(chunkPos);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {

    }
}
