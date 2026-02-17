package dev.cxd.dominium.mixin.client;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.utils.GhostRelatedStuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <T extends LivingEntity> void ghost$getPosOnScreen(T livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int i, CallbackInfo ci) {
        if (!(livingEntity instanceof PlayerEntity player)) return;

        if (!Dominium.GHOST_UUIDS.contains(player.getUuid())) return;

        if (player.getOffHandStack().getItem() == ModItems.DOMINIC_EFFIGY) return;

        if (player.getMainHandStack().getItem() == ModItems.DOMINIC_EFFIGY) {
            ci.cancel();
        }

        PlayerEntity localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer != null && !localPlayer.getUuid().equals(player.getUuid()) && GhostRelatedStuff.calculateGhostEffect(matrices, player)) {
            ci.cancel();
        }
    }
}


