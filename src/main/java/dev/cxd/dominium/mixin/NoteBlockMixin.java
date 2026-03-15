package dev.cxd.dominium.mixin;

import dev.cxd.dominium.block.misc.FunnyBoneBlock;
import dev.cxd.dominium.init.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    @Inject(method = "playNote", at = @At("HEAD"), cancellable = true)
    private void onPlayNote(Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);

        if (belowState.getBlock() instanceof FunnyBoneBlock) {
            int note = state.get(NoteBlock.NOTE);
            float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
            world.playSound(null, pos, ModSounds.BONE, SoundCategory.RECORDS, 3.0F, pitch);
            ci.cancel();
        }
    }
}