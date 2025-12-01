package dev.cxd.dominium.client.entity.eternal_divinity_chains;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EternalDivinityChainsModel<T extends EternalDivinityChainsEntity> extends SinglePartEntityModel<T> {

	public static final EntityModelLayer ETERNAL_DIVINITY_CHAINS = new EntityModelLayer(Identifier.of(Dominium.MOD_ID, "eternal_divinity_chains"), "main");

	private final ModelPart root;

	private final Animation idlingAnimation;
	private final Animation spawnAnimation;

	public EternalDivinityChainsModel(ModelPart root) {
		this.root = root.getChild("root");

		this.idlingAnimation = EternalDivinityChainsAnimations.IDLE;
		this.spawnAnimation = EternalDivinityChainsAnimations.SPAWN;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData chain1 = root.addChild("chain1", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5F, 0.0F, -0.4363F, 0.0F, 0.0F));

		ModelPartData bone1 = chain1.addChild("bone1", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData chain2 = root.addChild("chain2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.4363F, 0.0F, 0.0F));

		ModelPartData bone2 = chain2.addChild("bone2", ModelPartBuilder.create().uv(0, 19).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		super.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);

		updateAnimation(entity.idleAnimationState, idlingAnimation, animationProgress, 1f);
		updateAnimation(entity.spawnAnimationState, spawnAnimation, animationProgress, 1f);
	}
}