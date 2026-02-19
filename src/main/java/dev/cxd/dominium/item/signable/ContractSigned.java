package dev.cxd.dominium.item.signable;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ScreenParticleEffects;
import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

import java.awt.*;
import java.util.List;

public class ContractSigned extends CustomRarityItem implements ParticleEmitterHandler.ItemParticleSupplier {
    public ContractSigned(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String desc = ModComponents.getPlayerNameForSoulOwning(stack);
        String debugOne = ModComponents.getVesselUuid(stack);

        if ((desc == null || desc.isEmpty()) && (debugOne == null || debugOne.isEmpty())) {
            tooltip.add(Text.literal("/give?").formatted(Formatting.GOLD, Formatting.ITALIC));
        } else {
            if (desc != null && !desc.isEmpty()) {
                tooltip.add(Text.literal("Signed by " + desc).formatted(Formatting.AQUA));
            }

            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Debug UUID: " + debugOne).formatted(Formatting.DARK_GRAY));
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void spawnLateParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
        Color color = new Color(42, 201, 207);
        Color endColor = new Color(122, 245, 248);

        float distance = 10f;
        var rand = MinecraftClient.getInstance().world.getRandom();
        for (int i = 0; i < 1; i++) {
            float time = (((i == 1 ? 3.14f : 0) + ((level.getTime() + partialTick) * 0.075f)) % 6.28f);
            float scalar = 0.4f + 0.15f * 8f;
            if (time > 1.57f && time < 4.71f) {
                scalar *= Easing.QUAD_IN.ease(Math.abs(3.14f - time) / 1.57f, 0, 1, 1);
            }
            double xOffset = Math.sin(time) * distance;
            double yOffset = Math.cos(time) * distance * 0.5f;
            ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, target)
                    .setTransparencyData(GenericParticleData.create(0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(RandomHelper.randomBetween(rand, 0.2f, 0.4f)).setEasing(Easing.EXPO_OUT).build())
                    .setScaleData(GenericParticleData.create(RandomHelper.randomBetween(rand, 0.2f, 0.3f) * scalar, 0).setEasing(Easing.EXPO_OUT).build())
                    .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                    .setLifetime(RandomHelper.randomBetween(rand, 80, 120))
                    .setRandomOffset(0.1f)
                    .spawnOnStack(xOffset, yOffset);
        }
    }
}
