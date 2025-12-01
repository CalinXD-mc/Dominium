package dev.cxd.dominium.satin;

import dev.cxd.dominium.Dominium;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ShaderHandler {

    private static final Identifier BORDER_SHADER_ID =
            Identifier.of(Dominium.MOD_ID, "shaders/post/greyscale.json");
    private final ManagedShaderEffect borderShader =
            ShaderEffectManager.getInstance().manage(BORDER_SHADER_ID);

    private boolean shaderActive = false;
    private boolean callbackRegistered = false;

    public void updateClientShader(boolean shouldBeActive) {
        if (shaderActive && !shouldBeActive) {
            deactivateShader();
        } else if (!shaderActive && shouldBeActive) {
            activateShader();
        }
    }

    private void activateShader() {
        if (!shaderActive) {
            shaderActive = true;
            if (!callbackRegistered) {
                ShaderEffectRenderCallback.EVENT.register(this::renderShaderEffects);
                callbackRegistered = true;
                System.out.println("World border shader callback registered.");
            }
        }
    }

    public void deactivateShader() {
        shaderActive = false;
    }

    private void renderShaderEffects(float tickDelta) {
        if (shaderActive) {
            borderShader.render(tickDelta);
        }
    }
}