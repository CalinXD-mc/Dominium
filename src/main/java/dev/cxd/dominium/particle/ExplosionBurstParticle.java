package dev.cxd.dominium.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ExplosionBurstParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float spinSpeed;

    ExplosionBurstParticle(ClientWorld world, double x, double y, double z,
                           double velocityX, double velocityY, double velocityZ,
                           SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.spriteProvider = spriteProvider;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.maxAge = 100;

        this.scale = 0.75f;

        this.velocityMultiplier = 1.0f;

        this.gravityStrength = 0.0f;

        this.alpha = 1.0f;

        this.collidesWithWorld = false;

        this.prevAngle = world.random.nextFloat() * ((float) Math.PI * 2F);
        this.angle = this.prevAngle;

        this.spinSpeed = (world.random.nextFloat() - 0.5f) * 0.03f;

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        this.prevAngle = this.angle;
        this.angle += this.spinSpeed;

        float ageProgress = (float)this.age / (float)this.maxAge;
        this.alpha = 1.0f - MathHelper.clamp(ageProgress, 0.0f, 1.0f);

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld clientWorld,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            if (velocityX == 0 && velocityY == 0 && velocityZ == 0) {
                double angle = clientWorld.random.nextDouble() * Math.PI * 2;
                double pitch = (clientWorld.random.nextDouble() - 0.5) * Math.PI;
                double speed = 0.8 + clientWorld.random.nextDouble() * 0.7;

                velocityX = Math.cos(angle) * Math.cos(pitch) * speed;
                velocityY = Math.sin(pitch) * speed;
                velocityZ = Math.sin(angle) * Math.cos(pitch) * speed;
            }

            ExplosionBurstParticle particle = new ExplosionBurstParticle(
                    clientWorld, x, y, z,
                    velocityX, velocityY, velocityZ,
                    this.spriteProvider
            );
            particle.setAlpha(1.0f);
            return particle;
        }
    }
}