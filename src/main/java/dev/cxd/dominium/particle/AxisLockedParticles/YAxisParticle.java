package dev.cxd.dominium.particle.AxisLockedParticles;

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
public class YAxisParticle extends SpriteBillboardParticle {
    private int delay;

    YAxisParticle(ClientWorld world, double x, double y, double z, int delay, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.scale = 0.0F;
        this.delay = delay;
        this.gravityStrength = 0.0F;
        this.maxAge = 100;
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public float getSize(float tickDelta) {
        float progress = ((float)this.age + tickDelta) / (float)this.maxAge;
        return 128.0F * MathHelper.clamp(progress, 0.0f, 1.0f);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        if (this.delay > 0) {
            return;
        }

        float progress = ((float)this.age + tickDelta) / (float)this.maxAge;
        this.alpha = 1.0f - MathHelper.clamp(progress, 0.0f, 1.0f);

        this.buildGeometry(vertexConsumer, camera, tickDelta, (quaternion) -> {
            quaternion.mul(new Quaternionf().rotationX((float) (Math.PI / 2)));
        });

        this.buildGeometry(vertexConsumer, camera, tickDelta, (quaternion) -> {
            quaternion.mul(new Quaternionf().rotationX((float) (Math.PI / 2 + Math.PI)));
        });
    }

    private void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, Consumer<Quaternionf> rotator) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        Quaternionf quaternionf = new Quaternionf();
        rotator.accept(quaternionf);

        Vector3f[] vector3fs = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float size = this.getSize(tickDelta);

        for(int j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            vector3f.rotate(quaternionf);
            vector3f.mul(size);
            vector3f.add(f, g, h);
        }

        int light = this.getBrightness(tickDelta);
        this.vertex(vertexConsumer, vector3fs[0], this.getMaxU(), this.getMaxV(), light);
        this.vertex(vertexConsumer, vector3fs[1], this.getMaxU(), this.getMinV(), light);
        this.vertex(vertexConsumer, vector3fs[2], this.getMinU(), this.getMinV(), light);
        this.vertex(vertexConsumer, vector3fs[3], this.getMinU(), this.getMaxV(), light);
    }

    private void vertex(VertexConsumer vertexConsumer, Vector3f pos, float u, float v, int light) {
        vertexConsumer.vertex((double)pos.x(), (double)pos.y(), (double)pos.z())
                .texture(u, v)
                .color(this.red, this.green, this.blue, this.alpha)
                .light(light)
                .next();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i) {
            YAxisParticle particle = new YAxisParticle(clientWorld, d, e, f, 0, this.spriteProvider);
            particle.setAlpha(1.0f);
            return particle;
        }
    }
}