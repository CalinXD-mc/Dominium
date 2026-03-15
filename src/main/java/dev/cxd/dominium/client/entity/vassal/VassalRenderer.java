package dev.cxd.dominium.client.entity.vassal;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.client.entity.roofling.RooflingModel;
import dev.cxd.dominium.entity.RooflingEntity;
import dev.cxd.dominium.entity.VassalEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;

public class VassalRenderer extends LivingEntityRenderer<VassalEntity, VassalModel<VassalEntity>> {
    public VassalRenderer(EntityRendererFactory.Context context) {
        super(context, new VassalModel<>(context.getPart(VassalModel.VASSAL)), 0.5F);
    }

    @Override
    public Identifier getTexture(VassalEntity entity) {
        return new Identifier(Dominium.MOD_ID, "textures/entity/vassal.png");
    }

    @Override
    public void render(VassalEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public RenderLayer getRenderType(VassalEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture, true);
    }

    @Override protected boolean hasLabel(VassalEntity entity) {return false;}
}
