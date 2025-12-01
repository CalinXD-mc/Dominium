package dev.cxd.dominium.mixin;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModComponents;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static java.lang.Math.abs;

@Mixin(SpawnHelper.class)
public class MobBlockerMixin {

    private static final int RANGE = Dominium.SOUL_CANDLE_RANGE;

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void preventSpawn(ServerWorld world,
                                     SpawnGroup spawnGroup,
                                     StructureAccessor structureAccessor,
                                     ChunkGenerator chunkGenerator,
                                     SpawnSettings.SpawnEntry entry,
                                     BlockPos.Mutable pos,
                                     double random,
                                     CallbackInfoReturnable<Boolean> cir) {

        var optionalComponent = ModComponents.getChunkBlockCache(world);
        if (optionalComponent.isEmpty()) return;

        var component = optionalComponent.get();
        ChunkPos chunkPos = new ChunkPos(pos);

        for (int cx = chunkPos.x - 4; cx <= chunkPos.x + 4; cx++) {
            for (int cz = chunkPos.z - 4; cz <= chunkPos.z + 4; cz++) {
                List<BlockPos> blocks = component.getBlocksFromChunk(new ChunkPos(cx, cz), ModBlocks.SOUL_CANDLE);
                for (BlockPos b : blocks) {
                    if (Math.abs(b.getX() - pos.getX()) < RANGE &&
                            Math.abs(b.getY() - pos.getY()) < RANGE &&
                            Math.abs(b.getZ() - pos.getZ()) < RANGE) {

                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
