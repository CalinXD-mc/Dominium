package dev.cxd.dominium.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ChantDirectionalLockedParticle extends SpriteBillboardParticle {
    private static final Vector3f field_38334 = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
    private static final Vector3f field_38335 = new Vector3f(-1.0F, -1.0F, 0.0F);
    private static final float X_ROTATION = 1.0472F;
    private int delay;
    private final SpriteProvider spriteProvider;
    private boolean isActive; // Flag to track if the particle is active

    ChantDirectionalLockedParticle(ClientWorld world, double x, double y, double z, int delay, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.scale = 2.0F; // Initial scale
        this.delay = delay;
        this.gravityStrength = 0.0F;
        this.maxAge = 200;
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.isActive = true; // Initially inactive
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        if (this.delay <= 0) {
//            Quaternionf quaternionf = new Quaternionf();
//            quaternionf.rotationX((float) (Math.PI / 2));
//            quaternionf.rotationYXZ(-3.1415927F, -(float) (Math.PI / 2), 0.0F);
            this.buildGeometry(vertexConsumer, camera, tickDelta, (quaternion) -> {
                quaternion.mul((new Quaternionf()).rotationX((float) (Math.PI / 2)));
            });
            this.buildGeometry(vertexConsumer, camera, tickDelta, (quaternion) -> {
                quaternion.mul((new Quaternionf()).rotationYXZ((float) -(Math.PI), -(float) (Math.PI / 2), 0.0F));
            });
        }
    }

    private void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternionf> rotator) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0.0F, field_38334.x(), field_38334.y(), field_38334.z());
        rotator.accept(quaternionf);
        quaternionf.transform(field_38335);
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float i = this.getSize(tickDelta);

        int j;
        for(j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            vector3f.rotate(quaternionf);
            vector3f.mul(i);
            vector3f.add(f, g, h);
        }

        j = this.getBrightness(tickDelta);
        this.vertex(vertexConsumer, vector3fs[0], this.getMaxU(), this.getMaxV(), j);
        this.vertex(vertexConsumer, vector3fs[1], this.getMaxU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vector3fs[2], this.getMinU(), this.getMinV(), j);
        this.vertex(vertexConsumer, vector3fs[3], this.getMinU(), this.getMaxV(), j);
    }

    private void vertex(VertexConsumer vertexConsumer, Vector3f pos, float u, float v, int light) {
        vertexConsumer.vertex((double)pos.x(), (double)pos.y(), (double)pos.z()).texture(u, v).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return; // Skip logic if delay is active
        }

        int totalLifetime = 200; // Total lifetime in ticks (10 seconds)
        int fadeDuration = 40;  // Fade out over 2 seconds (40 ticks)
        int growthDuration = totalLifetime - fadeDuration; // Growth lasts for 8 seconds (160 ticks)

        // Calculate elapsed time
        int elapsedTime = this.age;

        if (elapsedTime <= growthDuration) { // First 8 seconds (160 ticks)
            // Gradually increase the scale (twice as large)
            this.scale = 4.0F + (float) elapsedTime / growthDuration * 4.0F; // Scale from 4.0F to 8.0F
        } else if (elapsedTime <= totalLifetime) { // Last 2 seconds (fade duration)
            // Fade out by reducing alpha
            float fadeProgress = (float) (elapsedTime - growthDuration) / fadeDuration;
            this.alpha = 1.0F - fadeProgress; // Alpha decreases from 1.0 to 0.0

            // Maintain the final size during fading
            this.scale = 8.0F;
        }

        super.tick(); // Call parent tick logic

        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
    }

    public void activate() {
        this.isActive = true; // Enable the particle
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ChantDirectionalLockedParticle particle = new ChantDirectionalLockedParticle(world, x, y + 0.5, z, (int) 2.0F, spriteProvider); // 1.0F is scale
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

}
