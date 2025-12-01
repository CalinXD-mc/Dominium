package dev.cxd.dominium.item;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.init.ModSounds;
import dev.cxd.dominium.init.ModStatusEffects;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class FixedDominicDaggerItem extends SwordItem implements MarkableItem, CanBanPeopleItem {
    private final ModRarities rarity;

    public FixedDominicDaggerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, ModRarities rarity) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.rarity = rarity;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        Color startColor = new Color(227, 214, 181);
        Color endColor = new Color(189, 165, 135);

        if (!player.getWorld().isClient()) {
            if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                if (entity instanceof PlayerEntity target) {
                    if (player.getHealth() >= 2) {
                        if (player.getWorld() instanceof ServerWorld serverWorld) {
                            MinecraftServer server = serverWorld.getServer();
                            Vec3d[] offsets = new Vec3d[] {
                                    new Vec3d(-2, 0, 0),
                                    new Vec3d(2, 0, 0),
                                    new Vec3d(-1, 0, 1),
                                    new Vec3d(1, 0, 1),
                                    new Vec3d(-1, 0, -1),
                                    new Vec3d(1, 0, -1)
                            };

                            for (Vec3d offset : offsets) {
                                Vec3d particlePos = player.getPos().add(offset);

                                ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                                        particlePos,
                                        startColor.getRGB(),
                                        endColor.getRGB(),
                                        "spetum_slam_particle"
                                );

                                PacketByteBuf bufMult = PacketByteBufs.create();
                                packetData.toBytes(bufMult);

                                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

                                ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, bufMult);
                            }

                            player.getItemCooldownManager().set(stack.getItem(), 20 * 600);

                            player.setStackInHand(hand, ItemStack.EMPTY);

                            target.addVelocity(777d, 50d, 777d);

                            DelayedTaskScheduler.schedule(server, 100, () -> {
                                server.execute(() -> {
                                    target.setVelocity(0D, 0D, 0D);
                                    target.teleport(777777D, 1500D, 777777D);
                                });
                            });

                            return ActionResult.SUCCESS;
                        }
                    }
                    return ActionResult.FAIL;

                }
                    return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public Text getName() {
        Text baseName = Text.translatable(this.getTranslationKey());
        Style style = Style.EMPTY.withFont(new Identifier("minecraft", "default"));

        return baseName.copy().setStyle(style.withColor(Formatting.GOLD));
    }

    @Override
    public Text getName(ItemStack stack) {
        Text baseName = super.getName(stack);

        return baseName.copy().setStyle(Style.EMPTY.withColor(rarity.color));
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
            tooltip.add(Text.literal("Forged by " + mark + ".").formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
