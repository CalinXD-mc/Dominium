package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void blockNoclipForGhosted(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        if (self.hasStatusEffect(ModStatusEffects.GHOSTED)) {
            self.noClip = false;
        }
    }
    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void preventNoclip(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        if (self.hasStatusEffect(ModStatusEffects.GHOSTED)) {
            self.noClip = false;
        }
    }
}
