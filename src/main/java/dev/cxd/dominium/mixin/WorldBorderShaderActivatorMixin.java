package dev.cxd.dominium.mixin;

import dev.cxd.dominium.satin.ShaderHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class WorldBorderShaderActivatorMixin {
    private static final ShaderHandler shaderHandler = new ShaderHandler();
    private static boolean wasDead = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        // Because we're inside ClientPlayerEntity, this cast is logically safe
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // Check if outside world border
        WorldBorder border = player.getWorld().getWorldBorder();
        Vec3d pos = player.getPos();
        boolean outsideBorder = !border.contains(BlockPos.ofFloored(pos));

        shaderHandler.updateClientShader(outsideBorder);

        if (!wasDead && player.isDead() && outsideBorder) {
            wasDead = true;
            System.out.println("Player died outside the border!");
        }

        if (wasDead && !player.isDead()) {
            wasDead = false;
        }
    }
}
