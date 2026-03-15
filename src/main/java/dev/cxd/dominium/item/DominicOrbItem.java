package dev.cxd.dominium.item;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ScreenParticleEffects;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.utils.ModRarities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

import java.awt.*;
import java.util.List;

public class DominicOrbItem extends CustomRarityItem implements ParticleEmitterHandler.ItemParticleSupplier  {
    public DominicOrbItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Color startColor = new Color(227, 214, 181);
        Color endColor = new Color(189, 165, 135);
        World world = context.getWorld();

        BlockPos bp = context.getBlockPos();
        BlockState bs = context.getWorld().getBlockState(bp);
        PlayerEntity player = context.getPlayer();

        if (bs.isOf(Blocks.STONE_BRICKS)) {
            world.setBlockState(bp, ModBlocks.DOMINIC_BRICKS.getDefaultState());
            world.playSound(null, bp, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 4F, 2F);
            world.playSound(null, bp, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 4F, 1F);

            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                    bp.toCenterPos(),
                    startColor.getRGB(),
                    endColor.getRGB(),
                    "block_outline_dominic_orb"
            );

            PacketByteBuf bufMult = PacketByteBufs.create();
            packetData.toBytes(bufMult);

            ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, bufMult);

            return ActionResult.SUCCESS;
        }

        if (bs.isOf(Blocks.CHISELED_STONE_BRICKS)) {
            world.setBlockState(bp, ModBlocks.CHISELED_DOMINIC_BRICKS.getDefaultState());
            world.playSound(null, bp, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 4F, 2F);
            world.playSound(null, bp, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 4F, 1F);

            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                    bp.toCenterPos(),
                    startColor.getRGB(),
                    endColor.getRGB(),
                    "block_outline_dominic_orb"
            );

            PacketByteBuf bufMult = PacketByteBufs.create();
            packetData.toBytes(bufMult);

            ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, bufMult);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (ModConfig.dominicOrbsInSurvival) {
            if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Obtained by killing Withers.").formatted(Formatting.GRAY).formatted(Formatting.OBFUSCATED));
            }
        } else {
            if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                tooltip.add(Text.literal("Obtained by Something a Something with Something.").formatted(Formatting.GRAY).formatted(Formatting.OBFUSCATED));
            }
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void spawnEarlyParticles(ScreenParticleHolder target, World level, float partialTick, ItemStack stack, float x, float y) {
        ScreenParticleEffects.spawnDominicOrbParticles(target, level, 1.05f, partialTick);
    }
}
