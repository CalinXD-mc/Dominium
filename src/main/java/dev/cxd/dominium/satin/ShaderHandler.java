package dev.cxd.dominium.satin;

import dev.cxd.dominium.Dominium;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.util.Identifier;

public class ShaderHandler {

    private static final Identifier BORDER_SHADER_ID =
            Identifier.of(Dominium.MOD_ID, "shaders/post/greyscale.json");

    private static final Identifier NETHER_ROOF_SHADER_ID =
            Identifier.of(Dominium.MOD_ID, "shaders/post/nether_roof.json");

    private final ManagedShaderEffect borderShader =
            ShaderEffectManager.getInstance().manage(BORDER_SHADER_ID);

    private final ManagedShaderEffect netherRoofShader =
            ShaderEffectManager.getInstance().manage(NETHER_ROOF_SHADER_ID);

    private boolean borderActive = false;
    private boolean netherRoofActive = false;
    private boolean callbackRegistered = false;

    public void updateBorderShader(boolean active) {
        borderActive = active;
        ensureCallback();
    }

    public void updateNetherRoofShader(boolean active) {
        netherRoofActive = active;
        ensureCallback();
    }

    private void ensureCallback() {
        if ((borderActive || netherRoofActive) && !callbackRegistered) {
            ShaderEffectRenderCallback.EVENT.register(this::renderShaderEffects);
            callbackRegistered = true;
        }
    }

    private void renderShaderEffects(float tickDelta) {
        if (borderActive) {
            borderShader.render(tickDelta);
        }
        if (netherRoofActive) {
            netherRoofShader.render(tickDelta);
        }
    }
}