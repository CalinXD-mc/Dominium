package dev.cxd.dominium.custome.packets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class ParticleSpawnPacket {
    private final Vec3d position;
    private final int startingColor;
    private final int endingColor;
    private final  String particleType;

    public ParticleSpawnPacket(Vec3d position, int startingColor, int endingColor, String particleType) {
        this.position = position;
        this.startingColor = startingColor;
        this.endingColor = endingColor;
        this.particleType = particleType;
    }

    public ParticleSpawnPacket(PacketByteBuf buf) {
        this.position = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.startingColor = buf.readInt();
        this.endingColor = buf.readInt();
        this.particleType = buf.readString(64);
    }

    public void toBytes(PacketByteBuf buf) {
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        buf.writeDouble(position.z);
        buf.writeInt(startingColor);
        buf.writeInt(endingColor);
        buf.writeString(particleType);
    }

    public void handle(MinecraftClient client) {
        client.execute(() -> {
            if (client.world != null) {
                Color startColor = new Color(startingColor);
                Color endColor = new Color(endingColor);
                spawnCustomParticle(client.world, position, startColor, endColor, particleType);
            }
        });
    }

    public static void spawnCustomParticle(World world, Vec3d pos, Color startColor, Color endColor, String type) {
        switch (type) {
            case "spetum_kill_particle" -> {
                float[] spinSpeeds = {0.2f, 0.4f, 0.6f, 0.8f, 1.0f};
                float baseTime = (world.getTime() * 0.2f) % 6.28f;

                for (int i = 0; i < spinSpeeds.length; i++) {
                    float speed = spinSpeeds[i];
                    float offset = baseTime + (i * 0.5f);

                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                            .setScaleData(GenericParticleData.create(3f, 30f, 60f).build())
                            .setTransparencyData(GenericParticleData.create(1f, 0.2f, 0f) // alpha at start, middle, end
                                    .setEasing(Easing.EXPO_OUT)
                                    .build())
                            .setColorData(ColorParticleData.create(startColor, endColor)
                                    .setCoefficient(1.4f)
                                    .setEasing(Easing.CUBIC_IN)
                                    .build())
                            .setLifetime(80)
                            .setSpinData(SpinParticleData.create(speed, speed + 0.2f, speed + 0.4f) // spin speed range
                                    .setSpinOffset(offset)
                                    .setEasing(Easing.QUARTIC_IN)
                                    .build())
                            .addMotion(0, 0, 0)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + 1, pos.z);
                }
                float beamHeight = 240f;
                float spacing = 0.5f;

                for (float y = -1; y <= beamHeight; y += spacing) {
                    WorldParticleBuilder.create(LodestoneParticleRegistry.THIN_EXTRUDING_SPARK_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.2f, 8f, 8f).build())
                            .setTransparencyData(GenericParticleData.create(0.8f, 0.4f, 0f)
                                    .setEasing(Easing.EXPO_OUT)
                                    .build())
                            .setColorData(ColorParticleData.create(startColor, endColor)
                                    .setCoefficient(1.2f)
                                    .setEasing(Easing.LINEAR)
                                    .build())
                            .setLifetime(80)
                            .addMotion(0, 0, 0)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + y, pos.z);
                }

                ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance
                        (100, pos, 477f, 777f, Easing.SINE_IN_OUT).setIntensity(3f, 7.5f, 0f));
            }
            case "eternal_divinity_1" -> {
                float beamHeight = 40f;
                float spacing = 0.5f;

                for (float y = -1; y <= beamHeight; y += spacing) {
                    WorldParticleBuilder.create(LodestoneParticleRegistry.THIN_EXTRUDING_SPARK_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.2f, 8f, 8f).build())
                            .setTransparencyData(GenericParticleData.create(0.8f, 0.4f, 0f)
                                    .setEasing(Easing.EXPO_OUT)
                                    .build())
                            .setColorData(ColorParticleData.create(startColor, endColor)
                                    .setCoefficient(1.2f)
                                    .setEasing(Easing.LINEAR)
                                    .build())
                            .setLifetime(80)
                            .addMotion(0, 0, 0)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + y, pos.z);
                }
            }
            case "eternal_divinity_2" -> {
                float beamHeight = 80f;
                float spacing = 0.45f;

                for (float y = -1; y <= beamHeight; y += spacing) {
                    WorldParticleBuilder.create(LodestoneParticleRegistry.THIN_EXTRUDING_SPARK_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.2f, 8f, 8f).build())
                            .setTransparencyData(GenericParticleData.create(0.8f, 0.4f, 0f)
                                    .setEasing(Easing.EXPO_OUT)
                                    .build())
                            .setColorData(ColorParticleData.create(startColor, endColor)
                                    .setCoefficient(1.2f)
                                    .setEasing(Easing.LINEAR)
                                    .build())
                            .setLifetime(80)
                            .addMotion(0, 0, 0)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + y, pos.z);
                }

                ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance
                        (20, pos, 477f, 50f, Easing.SINE_IN_OUT).setIntensity(1f, 2.5f, 0f));
            }
            case "eternal_divinity_3" -> {
                float beamHeight = 120f;
                float spacing = 0.4f;

                for (float y = -1; y <= beamHeight; y += spacing) {
                    WorldParticleBuilder.create(LodestoneParticleRegistry.THIN_EXTRUDING_SPARK_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.2f, 8f, 8f).build())
                            .setTransparencyData(GenericParticleData.create(0.8f, 0.4f, 0f)
                                    .setEasing(Easing.EXPO_OUT)
                                    .build())
                            .setColorData(ColorParticleData.create(startColor, endColor)
                                    .setCoefficient(1.2f)
                                    .setEasing(Easing.LINEAR)
                                    .build())
                            .setLifetime(80)
                            .addMotion(0, 0, 0)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + y, pos.z);
                }

                ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance
                        (100, pos, 477f, 250f, Easing.SINE_IN_OUT).setIntensity(2f, 5.0f, 0f));
            }
            case "spetum_jump_particle" -> WorldParticleBuilder.create(LodestoneParticleRegistry.THIN_EXTRUDING_SPARK_PARTICLE)
                    .setScaleData(GenericParticleData.create(2f, 2f, 2f).build())
                    .setTransparencyData(GenericParticleData.create(5f).build())
                    .setColorData(ColorParticleData.create(startColor, endColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN).build())
                    .setLifetime(40)
                    .addMotion(0, 0, 0)
                    .enableNoClip()
                    .spawn(world, pos.x, pos.y + 1, pos.z);
            case "spetum_slam_particle" -> {
                    WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.25f, 1.0f, 0.5f).build())
                            .setTransparencyData(GenericParticleData.create(10f, 100f).setEasing(Easing.EXPO_OUT).build())
                            .setColorData(ColorParticleData.create(startColor, endColor).setCoefficient(1.4f).setEasing(Easing.CUBIC_IN).build())
                            .setLifetime(30)
                            .setFullBrightLighting()
                            .setRandomMotion(0.03f, 0.03f, 0.03f)
                            .enableNoClip()
                            .setForceSpawn(true)
                            .spawn(world, pos.x, pos.y + 1, pos.z);
            }
            case "dominium_dagger_fixed_particles" -> {
                WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.25f, 1.0f, 0.5f).build())
                        .setTransparencyData(GenericParticleData.create(10f, 100f).setEasing(Easing.EXPO_OUT).build())
                        .setColorData(ColorParticleData.create(startColor, endColor).setCoefficient(1.4f).setEasing(Easing.CUBIC_IN).build())
                        .setLifetime(31)
                        .setFullBrightLighting()
                        .setRandomMotion(0.03f, 0.03f, 0.03f)
                        .enableNoClip()
                        .setForceSpawn(true)
                        .spawn(world, pos.x, pos.y + 1, pos.z);
            }
        }
    }
}