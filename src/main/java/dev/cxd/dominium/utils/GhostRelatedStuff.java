package dev.cxd.dominium.utils;

import dev.cxd.dominium.Dominium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GhostRelatedStuff {
    public static boolean calculateGhostEffect(MatrixStack matrixStack, LivingEntity livingEntity) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Vector3f position = new Vector3f();
        Vector3f positionEyes = new Vector3f(0f, (float) livingEntity.getEyeHeight(livingEntity.getPose()), 0f);
        position.mulPosition(matrix);
        positionEyes.mulPosition(matrix);

        double dist = livingEntity.getPos().distanceTo(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());

        int h = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int w = MinecraftClient.getInstance().getWindow().getScaledWidth();

        double x = (position.x / dist) * ((double) w / 2) + (double) w / 2;
        double yDown = (positionEyes.y / dist) * ((double) h / 2) + (double) h / 2;
        double yUp = (position.y / dist) * ((double) h / 2) + (double) h / 2;

        double borderX = w * Dominium.BORDER;
        double borderY = h * Dominium.BORDER * 2;

        return x > borderX && x < w - borderX && yDown > borderY && yUp < h - borderY;
    }

}
