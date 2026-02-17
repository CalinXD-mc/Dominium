package dev.cxd.dominium.client.entity.eternal_divinity_chains;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EternalDivinityChainsRenderer extends EntityRenderer<EternalDivinityChainsEntity> {

    private final EternalDivinityChainsModel<EternalDivinityChainsEntity> model;

    public EternalDivinityChainsRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new EternalDivinityChainsModel<>(context.getPart(EternalDivinityChainsModel.ETERNAL_DIVINITY_CHAINS));
        this.shadowRadius = 0.35f;
    }

    @Override
    public Identifier getTexture(EternalDivinityChainsEntity entity) {
        return new Identifier(Dominium.MOD_ID, "textures/entity/gilded_chains.png");
    }

    @Override
    public void render(EternalDivinityChainsEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        model.setAngles(entity, 0, 0, entity.age + tickDelta, 0, 0);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity))), light, 0, 1, 1, 1, 1);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}