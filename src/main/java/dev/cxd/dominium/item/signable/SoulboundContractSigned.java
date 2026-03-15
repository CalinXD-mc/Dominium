package dev.cxd.dominium.item.signable;

import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
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
import java.util.UUID;

public class SoulboundContractSigned extends CustomRarityItem implements ParticleEmitterHandler.ItemParticleSupplier {
    public SoulboundContractSigned(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        UUID targetUUID = UUID.fromString(ModComponents.getVesselUuid(stack));
        if (targetUUID == null) return;

        PlayerEntity target = world.getPlayerByUuid(targetUUID);
        if (target == null) return;

        if (entity.isSneaking() && world instanceof ServerWorld serverWorld && entity instanceof PlayerEntity player) {
            spawnConnectionParticles(serverWorld, player, target);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        String uuidStr = ModComponents.getVesselUuid(stack);
        if (uuidStr == null || uuidStr.isEmpty()) return TypedActionResult.pass(stack);

        if (world.isClient()) return TypedActionResult.success(stack);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(user.getInventory().selectedSlot);
        ServerPlayNetworking.send((ServerPlayerEntity) user, ModPackets.OPEN_SOULBOUND_SCREEN_ID, buf);

        return TypedActionResult.success(stack);
    }

    private void spawnConnectionParticles(ServerWorld world, PlayerEntity holder, PlayerEntity signer) {
        Vec3d holderPos = holder.getPos().add(0, holder.getHeight() / 2, 0);
        Vec3d signerPos = signer.getPos().add(0, signer.getHeight() / 2, 0);

        double distance = holderPos.distanceTo(signerPos);
        int particleCount = (int) (distance * 3);

        for (int i = 0; i <= particleCount; i++) {
            double progress = i / (double) particleCount;

            double x = holderPos.x + (signerPos.x - holderPos.x) * progress;
            double y = holderPos.y + (signerPos.y - holderPos.y) * progress;
            double z = holderPos.z + (signerPos.z - holderPos.z) * progress;

            world.spawnParticles(
                    ModParticles.DOMINIC_BALL,
                    x, y, z,
                    1,
                    0.01, 0.01, 0.01,
                    0.0
            );

            if (i % 3 == 0) {
                world.spawnParticles(
                        ParticleTypes.ENCHANT,
                        x, y, z,
                        1,
                        0.05, 0.05, 0.05,
                        0.1
                );
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String desc = ModComponents.getPlayerNameForSoulOwning(stack);
        String debugOne = ModComponents.getVesselUuid(stack);

        if ((desc == null || desc.isEmpty()) && (debugOne == null || debugOne.isEmpty())) {
            tooltip.add(Text.literal("/give?").formatted(Formatting.GOLD, Formatting.ITALIC));
        } else {
            if (desc != null && !desc.isEmpty()) {
                tooltip.add(Text.literal("Holds " + desc + "'s soul").formatted(Formatting.YELLOW));
            }
            if (debugOne != null && !debugOne.isEmpty() && MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Debug UUID: " + debugOne).formatted(Formatting.DARK_GRAY));
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void spawnLateParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
        Color color = new Color(184, 131, 70);
        Color endColor = new Color(255, 240, 154);

        float distance = 10f;
        var rand = MinecraftClient.getInstance().world.getRandom();
        for (int i = 0; i < 2; i++) {
            float time = (((i == 1 ? 3.14f : 0) + ((level.getTime() + partialTick) * 0.025f)) % 6.28f);
            float scalar = 0.4f + 0.15f * 12f;
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