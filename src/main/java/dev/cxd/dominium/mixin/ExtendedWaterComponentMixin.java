package dev.cxd.dominium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "moriyashiine.enchancement.common.component.entity.ExtendedWaterComponent", remap = false)
public class ExtendedWaterComponentMixin {
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void fixNullAttribute(CallbackInfo ci) {
        try {
        } catch (NullPointerException e) {
            ci.cancel();
        }
    }
}