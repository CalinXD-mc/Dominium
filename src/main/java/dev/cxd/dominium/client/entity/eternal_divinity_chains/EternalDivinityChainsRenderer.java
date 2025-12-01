package dev.cxd.dominium.client.entity.eternal_divinity_chains;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EternalDivinityChainsRenderer extends MobEntityRenderer<EternalDivinityChainsEntity, EternalDivinityChainsModel<EternalDivinityChainsEntity>> {


    public EternalDivinityChainsRenderer(EntityRendererFactory.Context context) {
        super(context, new EternalDivinityChainsModel<>(context.getPart(EternalDivinityChainsModel.ETERNAL_DIVINITY_CHAINS)), 0.35f);
    }

    @Override
    public Identifier getTexture(EternalDivinityChainsEntity entity) {
        return new Identifier(Dominium.MOD_ID, "textures/entity/gilded_chains.png");
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(EternalDivinityChainsEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return RenderLayer.getEntityTranslucent(getTexture(entity));
    }

    @Override
    public void render(EternalDivinityChainsEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
