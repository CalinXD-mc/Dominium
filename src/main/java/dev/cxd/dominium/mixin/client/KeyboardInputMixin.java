package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.init.ModStatusEffects;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void cancelInputForSoulStrain(boolean slowDown, float f, CallbackInfo ci) {
        KeyboardInput self = (KeyboardInput)(Object)this;

        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!client.player.hasStatusEffect(ModStatusEffects.SOUL_STRAIN)) return;

        self.pressingForward = false;
        self.pressingBack = false;
        self.pressingLeft = false;
        self.pressingRight = false;
        self.jumping = false;
        self.sneaking = false;
        self.movementForward = 0f;
        self.movementSideways = 0f;
    }
}
