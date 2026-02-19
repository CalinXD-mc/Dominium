package dev.cxd.dominium.client.entity.roofling;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.RooflingEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RooflingModel<T extends RooflingEntity> extends SinglePartEntityModel<T> {
    private final ModelPart r_leg;
    private final ModelPart l_leg;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart root;

    private final Animation idlingAnimation;
    private final Animation walkAnimation;
    public static final EntityModelLayer ROOFLING = new EntityModelLayer(Identifier.of(Dominium.MOD_ID, "roofling"), "main");

    public RooflingModel(ModelPart root) {
        this.root = root;
        this.r_leg = root.getChild("r_leg");
        this.l_leg = root.getChild("l_leg");
        this.r_arm = root.getChild("r_arm");
        this.l_arm = root.getChild("l_arm");
        this.torso = root.getChild("torso");
        this.head = root.getChild("head");

        this.idlingAnimation = RooflingAnimations.IDLE;
        this.walkAnimation = RooflingAnimations.WALK;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.NONE);

        ModelPartData r_leg = modelPartData.addChild("r_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));

        ModelPartData l_leg = modelPartData.addChild("l_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

        ModelPartData r_arm = modelPartData.addChild("r_arm", ModelPartBuilder.create().uv(40, 16).cuboid(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, 2.0F, 0.0F));

        ModelPartData l_arm = modelPartData.addChild("l_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 2.0F, 0.0F));

        ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        r_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        l_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        r_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        l_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        updateAnimation(entity.idleAnimationState, idlingAnimation, animationProgress, 1f);
        updateAnimation(entity.walkAnimationState, walkAnimation, animationProgress, 1f);
    }
}