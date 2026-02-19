package dev.cxd.dominium.client.lodestone_dark_magic_stuff;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneScreenParticleRenderType;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

import java.awt.*;

import static net.minecraft.util.math.MathHelper.nextFloat;

public class ScreenParticleEffects {

    public static void spawnEternalDivinityParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(184, 131, 70);
        Color endColor = new Color(255, 240, 154);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.SPARKLE, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.1f, 0.3f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, target)
                .setScaleData(GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0).setEasing(Easing.EXPO_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                .setSpinData(spinParticleData)
                .setLifetime(20 + rand.nextInt(8))
                .setRandomOffset(0.1f)
                .setRandomMotion(0.4f, 0.4f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0)
                .setLifetime((int) ((10 + rand.nextInt(2)) * timeMultiplier))
                .setSpinData(SpinParticleData.create(nextFloat(rand, 0.05f, 0.1f)).build())
                .setScaleData(GenericParticleData.create(0.8f + rand.nextFloat() * 0.4f, 0f).build())
                .setRandomMotion(0.01f, 0.01f)
                .spawnOnStack(0, 0);
    }

    public static void spawnBrokenEternalDivinityParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(230, 222, 179);
        Color endColor = new Color(155, 129, 98);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.SMOKE, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.075f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);
    }

    public static void spawnSoulOrbParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(42, 201, 207);
        Color endColor = new Color(122, 245, 248);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinA = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2)
                .setSpinOffset(0.025f * gameTime % 6.28f)
                .setEasing(Easing.EXPO_IN_OUT).build();
        SpinParticleData spinB = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2)
                .setSpinOffset((0.025f * gameTime + 3.14f) % 6.28f)
                .setEasing(Easing.EXPO_IN_OUT).build();

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.TWINKLE, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.1f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinA)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.TWINKLE, target)
                .setScaleData(GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0).setEasing(Easing.EXPO_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.1f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                .setSpinData(spinB)
                .setLifetime(20 + rand.nextInt(8))
                .setRandomOffset(0.1f)
                .setRandomMotion(0.4f, 0.4f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);
    }

    public static void spawnDominicOrbParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(184, 131, 70);
        Color endColor = new Color(255, 240, 154);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinA = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2)
                .setSpinOffset(0.045f * gameTime % 6.28f)
                .setEasing(Easing.EXPO_IN_OUT).build();
        SpinParticleData spinB = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2)
                .setSpinOffset((0.045f * gameTime + 3.14f) % 6.28f)
                .setEasing(Easing.EXPO_IN_OUT).build();

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.TWINKLE, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.1f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinA)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.TWINKLE, target)
                .setScaleData(GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0).setEasing(Easing.EXPO_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.1f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                .setSpinData(spinB)
                .setLifetime(20 + rand.nextInt(8))
                .setRandomOffset(0.1f)
                .setRandomMotion(0.4f, 0.4f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(0, 0);
    }

    public static void spawnGildedOnyxParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(184, 131, 70);
        Color endColor = new Color(255, 240, 154);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.SPARKLE, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(-2.5, 2.5);
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, target)
                .setScaleData(GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0).setEasing(Easing.EXPO_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                .setSpinData(spinParticleData)
                .setLifetime(20 + rand.nextInt(8))
                .setRandomOffset(0.1f)
                .setRandomMotion(0.4f, 0.4f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(-2.5, 2.5)
                .setLifetime((int) ((10 + rand.nextInt(2)) * timeMultiplier))
                .setSpinData(SpinParticleData.create(nextFloat(rand, 0.05f, 0.1f)).build())
                .setScaleData(GenericParticleData.create(0.8f + rand.nextFloat() * 0.4f, 0f).build())
                .setRandomMotion(0.01f, 0.01f)
                .spawnOnStack(-2.5, 2.5);
    }

    public static void spawnSoultrapSpetumParticles(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(184, 131, 70);
        Color endColor = new Color(255, 240, 154);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(2.5, -2.5);
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, target)
                .setScaleData(GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0).setEasing(Easing.EXPO_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                .setSpinData(spinParticleData)
                .setLifetime(20 + rand.nextInt(8))
                .setRandomOffset(0.1f)
                .setRandomMotion(0.4f, 0.4f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(2.5, -2.5)
                .setLifetime((int) ((10 + rand.nextInt(2)) * timeMultiplier))
                .setSpinData(SpinParticleData.create(nextFloat(rand, 0.05f, 0.1f)).build())
                .setScaleData(GenericParticleData.create(0.8f + rand.nextFloat() * 0.4f, 0f).build())
                .setRandomMotion(0.01f, 0.01f)
                .spawnOnStack(2.5, -2.5);
    }

    public static void spawnZombieNecklaceParticle(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(33, 63, 14);
        Color endColor = new Color(93, 120, 77);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(-0.5, 3.5);
    }

    public static void spawnSpiderNecklaceParticle(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(76, 0, 19);
        Color endColor = new Color(119, 0, 29);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(-0.5, 3.5);
    }

    public static void spawnEtherealNecklaceParticle(ScreenParticleHolder target, World world, float intensity, float partialTick) {
        float timeMultiplier = MathHelper.nextFloat(world.getRandom(), 0.9f, 1.4f);
        Color color = new Color(42, 201, 207);
        Color endColor = new Color(127, 255, 255);
        float gameTime = world.getTime() + partialTick;
        var rand = MinecraftClient.getInstance().world.getRandom();
        SpinParticleData spinParticleData = SpinParticleData.createRandomDirection(rand, 0, world.getRandom().nextBoolean() ? 1 : -2).setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build();
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
                .setScaleData(GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                .setTransparencyData(GenericParticleData.create(0.05f, 0.1f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                .setSpinData(spinParticleData)
                .setLifetime((int) ((10 + rand.nextInt(10)) * timeMultiplier))
                .setRandomOffset(0.05f)
                .setRandomMotion(0.05f, 0.05f)
                .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
                .spawnOnStack(-0.5, 3.5);
    }
}