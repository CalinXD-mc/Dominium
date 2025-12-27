package dev.cxd.dominium.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class ChunkBlockCacheComponent implements IChunkBlockCacheComponent, Component {
    private final World world;

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
    public void readFromNbt(NbtCompound nbt) {
        map.clear();

        NbtList chunksList = nbt.getList("Chunks", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < chunksList.size(); i++) {
            NbtCompound chunkData = chunksList.getCompound(i);

            int chunkX = chunkData.getInt("ChunkX");
            int chunkZ = chunkData.getInt("ChunkZ");
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

            NbtList blocksList = chunkData.getList("Blocks", NbtElement.COMPOUND_TYPE);
            Map<Block, Set<BlockPos>> chunkMap = new HashMap<>();

            for (int j = 0; j < blocksList.size(); j++) {
                NbtCompound blockData = blocksList.getCompound(j);

                String blockId = blockData.getString("BlockId");
                Identifier identifier = new Identifier(blockId);
                Block block = Registries.BLOCK.get(identifier);

                NbtList positionsList = blockData.getList("Positions", NbtElement.LONG_TYPE);
                Set<BlockPos> positions = new HashSet<>();

                for (int k = 0; k < positionsList.size(); k++) {
                    long posLong = (long) positionsList.getInt(k);
                    BlockPos pos = BlockPos.fromLong(posLong);
                    positions.add(pos);
                }

                if (!positions.isEmpty()) {
                    chunkMap.put(block, positions);
                }
            }

            if (!chunkMap.isEmpty()) {
                map.put(chunkPos, chunkMap);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtList chunksList = new NbtList();

        for (Map.Entry<ChunkPos, Map<Block, Set<BlockPos>>> chunkEntry : map.entrySet()) {
            ChunkPos chunkPos = chunkEntry.getKey();
            Map<Block, Set<BlockPos>> chunkMap = chunkEntry.getValue();

            NbtCompound chunkData = new NbtCompound();
            chunkData.putInt("ChunkX", chunkPos.x);
            chunkData.putInt("ChunkZ", chunkPos.z);

            NbtList blocksList = new NbtList();

            for (Map.Entry<Block, Set<BlockPos>> blockEntry : chunkMap.entrySet()) {
                Block block = blockEntry.getKey();
                Set<BlockPos> positions = blockEntry.getValue();

                if (positions.isEmpty()) continue;

                NbtCompound blockData = new NbtCompound();
                Identifier blockId = Registries.BLOCK.getId(block);
                blockData.putString("BlockId", blockId.toString());

                NbtList positionsList = new NbtList();

                for (BlockPos pos : positions) {
                    positionsList.add(NbtLong.of(pos.asLong()));
                }

                blockData.put("Positions", positionsList);
                blocksList.add(blockData);
            }

            chunkData.put("Blocks", blocksList);
            chunksList.add(chunkData);
        }

        nbt.put("Chunks", chunksList);
    }
}