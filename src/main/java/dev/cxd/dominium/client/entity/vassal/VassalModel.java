package dev.cxd.dominium.client.entity.vassal;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.VassalEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class VassalModel<T extends VassalEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart righthorn;
    private final ModelPart righthornjoint;
    private final ModelPart lefthorn;
    private final ModelPart lefthornjoint;
    private final ModelPart eyes;
    private final ModelPart rightarm;
    private final ModelPart rightarmjoint;
    private final ModelPart blade;
    private final ModelPart leftarm;
    private final ModelPart leftarmjoint;
    private final ModelPart flap;
    private final ModelPart backflap;
    private final ModelPart rightleg;
    private final ModelPart rightlegjoint;
    private final ModelPart leftleg;
    private final ModelPart leftlegjoint;
    public static final EntityModelLayer VASSAL = new EntityModelLayer(new Identifier(Dominium.MOD_ID, "vassal"), "main");

    public VassalModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.horns = this.head.getChild("horns");
        this.righthorn = this.horns.getChild("righthorn");
        this.righthornjoint = this.righthorn.getChild("righthornjoint");
        this.lefthorn = this.horns.getChild("lefthorn");
        this.lefthornjoint = this.lefthorn.getChild("lefthornjoint");
        this.eyes = this.head.getChild("eyes");
        this.rightarm = this.root.getChild("rightarm");
        this.rightarmjoint = this.rightarm.getChild("rightarmjoint");
        this.blade = this.rightarmjoint.getChild("blade");
        this.leftarm = this.root.getChild("leftarm");
        this.leftarmjoint = this.leftarm.getChild("leftarmjoint");
        this.flap = this.root.getChild("flap");
        this.backflap = this.root.getChild("backflap");
        this.rightleg = this.root.getChild("rightleg");
        this.rightlegjoint = this.rightleg.getChild("rightlegjoint");
        this.leftleg = this.root.getChild("leftleg");
        this.leftlegjoint = this.leftleg.getChild("leftlegjoint");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(38, 29).cuboid(-5.0F, -32.0F, -4.0F, 10.0F, 10.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData root_r1 = root.addChild("root_r1", ModelPartBuilder.create().uv(0, 29).cuboid(-6.0F, -13.0F, -2.0F, 12.0F, 14.0F, 7.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, -30.0F, 1.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData root_r2 = root.addChild("root_r2", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -9.0F, -6.0F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -30.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -38.0F, 0.0F));

        ModelPartData head_r1 = head.addChild("head_r1", ModelPartBuilder.create().uv(0, 112).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.1F))
                .uv(46, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData horns = head.addChild("horns", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 1.0F));

        ModelPartData righthorn = horns.addChild("righthorn", ModelPartBuilder.create().uv(28, 48).cuboid(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -7.0F, 1.0F, -0.7854F, 2.2253F, 0.0F));

        ModelPartData righthornjoint = righthorn.addChild("righthornjoint", ModelPartBuilder.create().uv(31, 29).cuboid(-2.0F, 7.0F, -6.0F, 4.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 21).cuboid(-2.0F, 5.0F, -4.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 79).mirrored().cuboid(-2.0F, 2.5F, -9.0F, 4.0F, 7.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 89).mirrored().cuboid(-1.5F, 2.0F, -8.5F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 0.0F, 1.0F));

        ModelPartData lefthorn = horns.addChild("lefthorn", ModelPartBuilder.create().uv(28, 48).cuboid(-2.0F, -2.0F, -8.0F, 4.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -7.0F, 1.0F, -0.7854F, -2.2253F, 0.0F));

        ModelPartData lefthornjoint = lefthorn.addChild("lefthornjoint", ModelPartBuilder.create().uv(0, 79).cuboid(-2.0F, 2.5F, -9.0F, 4.0F, 7.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 89).cuboid(-1.5F, 2.0F, -8.5F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(35, 0).cuboid(-2.0F, 7.0F, -6.0F, 4.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(10, 21).cuboid(-2.0F, 5.0F, -4.0F, 4.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 1.0F));

        ModelPartData eyes = head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData eyes_r1 = eyes.addChild("eyes_r1", ModelPartBuilder.create().uv(32, 112).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.05F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData rightarm = root.addChild("rightarm", ModelPartBuilder.create().uv(56, 48).cuboid(-8.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F, new Dilation(0.0F))
                .uv(78, 39).cuboid(-5.0F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.05F))
                .uv(0, 89).mirrored().cuboid(-4.0F, 9.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)).mirrored(false), ModelTransform.pivot(-6.0F, -38.0F, 1.0F));

        ModelPartData rightarmjoint = rightarm.addChild("rightarmjoint", ModelPartBuilder.create().uv(76, 26).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, 10.0F, 0.0F));

        ModelPartData blade = rightarmjoint.addChild("blade", ModelPartBuilder.create().uv(18, 41).cuboid(0.0F, -2.5F, -4.0F, 0.0F, 5.0F, 9.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -4.0F, -5.0F, 2.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 119).cuboid(-0.5F, -1.5F, -28.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(120, 117).cuboid(-0.5F, -2.5F, -27.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 117).cuboid(-0.5F, -0.5F, -29.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(78, 99).cuboid(-0.5F, -2.5F, -29.0F, 1.0F, 5.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.5F, 0.0F));

        ModelPartData leftarm = root.addChild("leftarm", ModelPartBuilder.create().uv(48, 16).cuboid(0.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F, new Dilation(0.0F))
                .uv(78, 0).cuboid(0.0F, 4.0F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.05F))
                .uv(0, 89).cuboid(1.0F, 9.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.05F)), ModelTransform.pivot(6.0F, -38.0F, 1.0F));

        ModelPartData leftarmjoint = leftarm.addChild("leftarmjoint", ModelPartBuilder.create().uv(15, 76).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(2.5F, 10.0F, 0.0F));

        ModelPartData flap = root.addChild("flap", ModelPartBuilder.create().uv(61, 85).cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, -3.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData backflap = root.addChild("backflap", ModelPartBuilder.create().uv(61, 85).cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData rightleg = root.addChild("rightleg", ModelPartBuilder.create().uv(76, 11).cuboid(-3.0F, 2.0F, -3.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 89).mirrored().cuboid(-2.0F, 10.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-3.0F, -21.0F, 1.0F));

        ModelPartData rightleg_r1 = rightleg.addChild("rightleg_r1", ModelPartBuilder.create().uv(22, 62).cuboid(-5.0F, -1.0F, -3.5F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData rightlegjoint = rightleg.addChild("rightlegjoint", ModelPartBuilder.create().uv(41, 75).cuboid(-2.5F, 0.0F, -3.0F, 5.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 11.0F, 0.0F));

        ModelPartData leftleg = root.addChild("leftleg", ModelPartBuilder.create().uv(69, 70).cuboid(-3.0F, 2.0F, -2.5F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 89).cuboid(-2.0F, 10.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -21.0F, 1.0F));

        ModelPartData leftleg_r1 = leftleg.addChild("leftleg_r1", ModelPartBuilder.create().uv(50, 61).cuboid(-1.0F, -1.0F, -3.0F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        ModelPartData leftlegjoint = leftleg.addChild("leftlegjoint", ModelPartBuilder.create().uv(0, 64).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 11.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    private final Vector3f animationVec = new Vector3f();
    private long attackStartMs = -1;

    private long activateStartMs = -1;

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        long nowMs = System.currentTimeMillis();
        long loopMs = (long)(animationProgress * 50f);

        if (entity.isDormant()) {
            if (entity.getAttackState() == 1) {
                if (activateStartMs < 0) {
                    activateStartMs = nowMs;
                }
                long elapsedMs = nowMs - activateStartMs;
                AnimationHelper.animate(this, VassalAnimation.ACTIVATE, elapsedMs, 1.0f, animationVec);
            } else {
                activateStartMs = -1;
                AnimationHelper.animate(this, VassalAnimation.DORMANT, loopMs, 1.0f, animationVec);
            }
            return;
        }

        activateStartMs = -1;

        if (entity.getAttackState() == 2) {
            if (attackStartMs < 0 || entity.age == entity.attackStartAge) {
                attackStartMs = nowMs;
            }
            long elapsedMs = nowMs - attackStartMs;
            AnimationHelper.animate(this, VassalAnimation.ATTACK, elapsedMs, 1.0f, animationVec);
        } else {
            attackStartMs = -1;

            if (limbDistance > 0.01f) {
                AnimationHelper.animate(this, VassalAnimation.WALK, loopMs, 1.0f, animationVec);
            } else {
                AnimationHelper.animate(this, VassalAnimation.IDLE, loopMs, 1.0f, animationVec);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}