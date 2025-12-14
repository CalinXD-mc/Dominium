package dev.cxd.dominium.item.ban_items;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.custome.packets.ParticleSpawnPacket;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class EternalDivinityItem extends CustomRarityItem implements MarkableItem, CanBanPeopleItem {

    public EternalDivinityItem(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    public static int getDurability(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("CustomDurability")) {
            return stack.getNbt().getInt("CustomDurability");
        }
        return 3;
    }

    public static void setDurability(ItemStack stack, int durability) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("CustomDurability", durability);
    }

    private void decreaseDurability(ItemStack stack) {
        int durability = getDurability(stack) - 1;
        setDurability(stack, durability);
        if (durability <= 0) stack.setCount(0);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);

        if (!stack.isOf(ModItems.ETERNAL_DIVINITY)) return;
        int durability = EternalDivinityItem.getDurability(stack);
        if (durability != 0) return;

        stack.setCount(0);
    }

    public static void spawnParticles(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        Color startColor = new Color(248, 209, 109);
        Color endColor = new Color(211, 149, 77);
        Vec3d pos = victim.getPos();

        if (!victim.getWorld().isClient && victim.getWorld() instanceof ServerWorld serverWorld) {
            int durability = getDurability(stack);
            String particleType = switch (durability) {
                case 3 -> "eternal_divinity_1";
                case 2 -> "eternal_divinity_2";
                case 1 -> "eternal_divinity_3";
                default -> null;
            };

            if (particleType != null) {
                ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                        pos, startColor.getRGB(), endColor.getRGB(), particleType
                );

                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    packetData.toBytes(buf);
                    ServerPlayNetworking.send(player, ModPackets.PARTICLE_SPAWN_ID, buf);
                }
            }
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int max = 3;
        int current = getDurability(stack);
        return Math.round(13.0F * current / max);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xF4CD69; // orange bar color
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
