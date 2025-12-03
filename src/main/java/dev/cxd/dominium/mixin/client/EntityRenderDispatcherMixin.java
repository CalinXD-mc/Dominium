package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.Dominium;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void cancelShadow(MatrixStack matrices, VertexConsumerProvider buffer, Entity entity,
                                     float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player && Dominium.GHOST_UUIDS.contains(player.getUuid())) {
            ci.cancel();
        }
    }
}