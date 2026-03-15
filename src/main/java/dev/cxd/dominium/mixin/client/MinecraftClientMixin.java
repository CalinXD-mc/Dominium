package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.init.ModStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void cancelAttackForSoulStrain(CallbackInfoReturnable<Boolean> ci) {
        net.minecraft.client.MinecraftClient client = (net.minecraft.client.MinecraftClient)(Object)this;
        if (client.player != null && client.player.hasStatusEffect(ModStatusEffects.SOUL_STRAIN)) {
            ci.cancel();
        }
    }

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void cancelUseForSoulStrain(CallbackInfo ci) {
        net.minecraft.client.MinecraftClient client = (net.minecraft.client.MinecraftClient)(Object)this;
        if (client.player != null && client.player.hasStatusEffect(ModStatusEffects.SOUL_STRAIN)) {
            ci.cancel();
        }
    }
}