package dev.cxd.dominium.item.ban_items;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class BrokenEternalDivinityItem extends CustomRarityItem implements MarkableItem, CanBanPeopleItem {

    public BrokenEternalDivinityItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    public static void spawnParticles(LivingEntity victim) {
        Color startColor = new Color(248, 209, 109);
        Color endColor = new Color(211, 149, 77);
        Vec3d pos = victim.getPos();

        if (!victim.getWorld().isClient && victim.getWorld() instanceof ServerWorld serverWorld) {
            ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                    pos, startColor.getRGB(), endColor.getRGB(), "rotated_beam_south_45"
            );

            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                packetData.toBytes(buf);
                ServerPlayNetworking.send(player, ModPackets.PARTICLE_SPAWN_ID, buf);
            }
        }
    }

    @Override
    public void setMark(ItemStack stack, String mark) {
        stack.getOrCreateNbt().putString("Mark", mark);
    }

    @Override
    public String getMark(ItemStack stack) {
        return stack.hasNbt() ? stack.getNbt().getString("Mark") : "";
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String mark = getMark(stack);
        if (!mark.isEmpty()) {
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}