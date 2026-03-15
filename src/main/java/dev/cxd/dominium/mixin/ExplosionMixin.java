package dev.cxd.dominium.mixin;

import dev.cxd.dominium.block.ObeliskBlock;
import dev.cxd.dominium.block.entity.ObeliskBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow private World world;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void removeClaimedBlocksFromExplosion(boolean particles, CallbackInfo ci) {
        Explosion self = (Explosion)(Object)this;
        List<BlockPos> affectedBlocks = self.getAffectedBlocks();
        if (affectedBlocks.isEmpty()) return;

        List<ObeliskBlockEntity> nearbyClaims = new ArrayList<>();

        int searchRadius = ObeliskBlockEntity.OBELISK_PROTECTION_RADIUS + 1;
        int searchHeight = ObeliskBlockEntity.OBELISK_PROTECTION_HEIGHT + 1;

        BlockPos center = affectedBlocks.get(0);
        for (BlockPos p : BlockPos.iterateOutwards(center, searchRadius + 16, searchHeight + 16, searchRadius + 16)) {
            BlockState bs = world.getBlockState(p);
            if (bs.getBlock() instanceof ObeliskBlock &&
                    bs.get(ObeliskBlock.PART) == ObeliskBlock.ObeliskPart.LOWER) {
                if (world.getBlockEntity(p) instanceof ObeliskBlockEntity be) {
                    nearbyClaims.add(be);
                }
            }
        }

        if (nearbyClaims.isEmpty()) return;

        affectedBlocks.removeIf(pos -> {
            for (ObeliskBlockEntity be : nearbyClaims) {
                if (be.isInClaimRange(pos)) return true;
            }
            return false;
        });
    }
}