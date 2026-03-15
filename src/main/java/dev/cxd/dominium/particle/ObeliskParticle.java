package dev.cxd.dominium.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ObeliskParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    private final float rotationSpeed;

    protected ObeliskParticle(ClientWorld world, double x, double y, double z,
                              SpriteProvider spriteProvider) {
        super(world, x, y, z, 0, 0, 0);

        this.spriteProvider = spriteProvider;

        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.velocityMultiplier = 0f;
        this.gravityStrength = 0f;
        this.collidesWithWorld = false;

        this.alpha = 0.25f + random.nextFloat() * 0.75f;

        this.rotationSpeed = 0.1f + random.nextFloat() * 0.4f;
        this.prevAngle = random.nextFloat() * (float) Math.PI * 2f;
        this.angle = this.prevAngle;

        this.scale = 0.05f + random.nextFloat() * 0.15f;
        this.maxAge = 100 + random.nextInt(6);

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAngle = this.angle;
        this.angle += rotationSpeed;
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

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new ObeliskParticle(world, x, y, z, spriteProvider);
        }
    }
}