package dev.cxd.dominium.mixin;

import dev.cxd.dominium.init.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityDeathMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void dropSoulOrbs(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Check block below player
        BlockPos posBelow = player.getBlockPos().down();
        boolean onSoulBlock = player.getWorld().getBlockState(posBelow).isOf(Blocks.SOUL_SAND)
                || player.getWorld().getBlockState(posBelow).isOf(Blocks.SOUL_SOIL);

        if (onSoulBlock) {
            // Drop 2-5 soul orbs
            int amount = 2 + player.getRandom().nextInt(4); // 2-5

            for (int i = 0; i < amount; i++) {
                player.dropItem(ModItems.DOMINIC_ORB.getDefaultStack(), false);
            }
        }
    }
}