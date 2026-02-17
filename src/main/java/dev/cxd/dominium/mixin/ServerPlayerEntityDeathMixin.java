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
        
    }
}