package dev.cxd.dominium.item.ban_items;

import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.init.ModPackets;
import dev.cxd.dominium.utils.MarkableItem;
import dev.cxd.dominium.utils.ModRarities;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class SoultrapSpetumItem extends SwordItem implements MarkableItem {
    private final ModRarities rarity;

    public SoultrapSpetumItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, ModRarities rarity) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.rarity = rarity;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            if (player.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.pass(player.getStackInHand(hand));

            ItemStack stack = player.getStackInHand(hand);
            Vec3d lookDir = player.getRotationVec(1.0F).normalize();

            Color startColor = new Color(248, 209, 109);
            Color endColor = new Color(184, 131, 70);

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            if (player.isOnGround()) {
                // upward launch
                Vec3d launch = new Vec3d(0, 1.0, 0);
                player.addVelocity(launch.x, launch.y, launch.z);
                player.velocityModified = true;
                player.fallDistance = 0.0F;

                ParticleSpawnPacketData jump_particle_packet = new ParticleSpawnPacketData(
                        player.getPos(),
                        startColor.getRGB(),
                        endColor.getRGB(),
                        "spetum_jump_particle"
                );

                jump_particle_packet.toBytes(buf);
                ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, buf);
                world.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_PHANTOM_FLAP,
                        SoundCategory.PLAYERS,
                        2.0F,
                        2.0F
                );
            } else {
                // 45° downward slam
                Vec3d downward45 = new Vec3d(lookDir.x, -Math.tan(45.0F), lookDir.z).normalize().multiply(4.0);
                player.addVelocity(downward45.x, downward45.y, downward45.z);
                player.velocityModified = true;
                player.fallDistance = 0.0F;

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

                    PacketByteBuf bufMult = PacketByteBufs.create(); // fresh buffer each time
                    packetData.toBytes(bufMult);

                    ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, bufMult);
                }


                stack.getOrCreateNbt().putBoolean("SlamActive", true);
                stack.getOrCreateNbt().putBoolean("HitSomething", false);
                stack.getOrCreateNbt().putBoolean("DownwardPhase", true);
                world.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                        SoundCategory.PLAYERS,
                        1.0F,
                        0.5F
                );
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient || !(entity instanceof PlayerEntity player)) return;

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!selected) {
            if (nbt.getBoolean("SlamActive") || nbt.getBoolean("DownwardPhase")) {
                player.setInvulnerable(false);
                nbt.putBoolean("SlamActive", false);
                nbt.putBoolean("DownwardPhase", false);
                nbt.putBoolean("HitSomething", false);
            }
            return;
        }

        boolean downward = nbt.getBoolean("DownwardPhase");
        boolean hasSlam = nbt.getBoolean("SlamActive");

        if (hasSlam && downward) {
            player.setInvulnerable(true);

            if (!player.isOnGround()) {
                Box area = player.getBoundingBox().expand(1.35);
                List<LivingEntity> mobs = world.getEntitiesByClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive());

                if (!mobs.isEmpty()) {
                    for (LivingEntity mob : mobs) {
                        mob.damage(world.getDamageSources().playerAttack(player), 6.0F);
                        world.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                SoundCategory.PLAYERS,
                                1.0F,
                                1.0F
                        );
                    }
                    nbt.putBoolean("HitSomething", true);
                }
            } else {
                player.setInvulnerable(false);
                boolean hitSomething = nbt.getBoolean("HitSomething");

                nbt.putBoolean("DownwardPhase", false);
                nbt.putBoolean("SlamActive", false);
                nbt.putBoolean("HitSomething", false);

                if (hitSomething) {
                    // 1 second A_REGULAR_UUID (20 ticks)
                    player.getItemCooldownManager().set(this, 15);

                    Vec3d lookDir = player.getRotationVec(1.0F).normalize();
                    Vec3d backward = lookDir.multiply(-1).normalize();
                    Vec3d bounce = new Vec3d(backward.x, Math.tan(22.5F), backward.z).normalize().multiply(1.5);
                    player.setVelocity(bounce);
                    player.velocityModified = true;
                    player.fallDistance = 0.0F;
                } else {
                    // 5 second A_REGULAR_UUID (100 ticks)
                    player.getItemCooldownManager().set(this, 160);
                }
            }
        } else {
            player.setInvulnerable(false);
        }
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
            tooltip.add(Text.literal(mark).formatted(Formatting.GOLD));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}