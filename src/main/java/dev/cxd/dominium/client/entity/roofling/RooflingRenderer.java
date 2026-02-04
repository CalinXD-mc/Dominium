package dev.cxd.dominium.client.entity.roofling;

import dev.cxd.dominium.Dominium;

import dev.cxd.dominium.entity.RooflingEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RooflingRenderer extends LivingEntityRenderer<RooflingEntity, RooflingModel<RooflingEntity>> {

    public RooflingRenderer(EntityRendererFactory.Context context) {
        super(context, new RooflingModel<>(context.getPart(RooflingModel.ROOFLING)), 0.5F);
    }

    @Override
    public Identifier getTexture(RooflingEntity entity) {
        return new Identifier(Dominium.MOD_ID, "textures/entity/roofling.png");
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(RooflingEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucent(getTexture(entity));
    }

    @Override
    public void render(RooflingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
