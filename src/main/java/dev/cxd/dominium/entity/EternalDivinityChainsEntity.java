package dev.cxd.dominium.entity;

import com.mojang.authlib.GameProfile;
import dev.cxd.dominium.client.lodestone_dark_magic_stuff.ParticleSpawnPacketData;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.utils.CanBanPeopleItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.BanEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.awt.*;
import java.util.UUID;

public class EternalDivinityChainsEntity extends MobEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public final AnimationState spawnAnimationState = new AnimationState();

    private UUID boundPlayer;
    private int lifetimeTicks; // 3 minutes

    public EternalDivinityChainsEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
        this.setNoGravity(true);
    }

    public void setBoundPlayer(UUID uuid) {
        if (uuid == null) return;
        this.boundPlayer = uuid;
    }

    public UUID getBoundPlayer() {
        return boundPlayer;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker(); // this sets up health and other MobEntity/LivingEntity trackers
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (boundPlayer != null) {
            nbt.putUuid("BoundPlayer", boundPlayer);
        }
        nbt.putInt("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("BoundPlayer")) {
            boundPlayer = nbt.getUuid("BoundPlayer");
        }
        if (nbt.contains("LifetimeTicks")) {
            lifetimeTicks = nbt.getInt("LifetimeTicks");
        }
    }

    @Override
    public void tick() {
        super.tick();

        //animations
        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }

        if (this.isRemoved()) return;

        //existence checks
        if (boundPlayer == null) {
            if (age > 5) {
                if (!getWorld().isClient()) {
                    discard();
                }
            }
            return;
        }

        PlayerEntity bound = getWorld().getPlayerByUuid(boundPlayer);

        if (bound == null) {
            if (age > 40) {
                if (!getWorld().isClient()) {
                    discard();
                }
            }
            return;
        }

        // Teleport to the player
        this.setPos(bound.getX(), bound.getY(), bound.getZ());

        // Freeze in place relative to player
        setVelocity(Vec3d.ZERO);

        // Sync lifetime with Soul Strain duration
        if (!getWorld().isClient()) {
            StatusEffectInstance soulStrain = bound.getStatusEffect(ModStatusEffects.SOUL_STRAIN);

            if (soulStrain != null) {
                lifetimeTicks = soulStrain.getDuration();
            } else {
                // No soul strain effect = despawn
                bound.velocityModified = true;
                bound.setVelocity(0, 0, 0);
                bound.stopRiding();
                discard();
                return;
            }
        }

        // Lifetime countdown (now synced with Soul Strain)
        lifetimeTicks--;
        if (lifetimeTicks <= 0) {
            if (!getWorld().isClient()) {
                if (bound != null) {
                    bound.velocityModified = true;
                    bound.setVelocity(0, 0, 0);
                    bound.stopRiding();
                }
                discard();
            }
        }

        if (!getWorld().isClient()) {
            bound.setVelocity(Vec3d.ZERO);
            bound.velocityModified = true;
            bound.fallDistance = 0; // stop fall damage
        }
    }

    private void setupAnimationStates() {
        if (this.age == 1) {
            this.spawnAnimationState.start(this.age);
        }

        // Idle animation loop
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 20;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }


    @Override
    public void onRemoved() {
        super.onRemoved();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void pushAwayFrom(Entity entity) {

    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient()) {
            if (boundPlayer != null) {
                PlayerEntity bound = getWorld().getPlayerByUuid(boundPlayer);

                if (bound != null && !player.getUuid().equals(boundPlayer)) {
                    var stack = player.getStackInHand(hand);

                    if (!stack.isEmpty() && stack.getItem() instanceof CanBanPeopleItem) {
                        if (bound.hasStatusEffect(ModStatusEffects.SOUL_STRAIN)) {
                            if (bound instanceof ServerPlayerEntity serverBound) {
                                serverBound.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);
                                banOrSpectator(serverBound);
                            }

                            World world = player.getWorld();
                            BlockPos blockPos = player.getBlockPos();
                            Vec3d originalPosition = Vec3d.ofCenter(blockPos);

                            Color startColor = new Color(248, 209, 109);
                            Color endColor = new Color(211, 149, 77);

                            ParticleSpawnPacketData packetData = new ParticleSpawnPacketData(
                                    originalPosition, startColor.getRGB(), endColor.getRGB(), "spetum_kill_particle"
                            );

                            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                            packetData.toBytes(buf);

                            if (player instanceof ServerPlayerEntity serverPlayer) {
                                ServerPlayNetworking.send(serverPlayer, ModPackets.PARTICLE_SPAWN_ID, buf);
                            }

                            if (bound instanceof ServerPlayerEntity serverBound) {
//                                serverBound.changeGameMode(GameMode.SPECTATOR);
                                serverBound.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);

                                Text message = Text.literal(serverBound.getName().getString() + "'s existence was forfeited").formatted(Formatting.WHITE);
                                for (PlayerEntity p : serverBound.getWorld().getPlayers()) {
                                    p.sendMessage(message, false);
                                }

                                for (PlayerEntity p : serverBound.getWorld().getPlayers()) {
                                    p.playSound(ModSounds.DOMINIC_BOOM, 1.0F, 0.5F);
                                }
                                banOrSpectator(serverBound);
                            }

                            player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.REGRET, 20 * 5, 0, false, false, true));

                            ItemStack stackInHand = player.getStackInHand(hand);

                            if (stackInHand.isOf(ModItems.DOMINIC_DAGGER)) {
                                if (!world.isClient()) {
                                    double radius = 8.0D;
                                    int noOfExplosions = 24;

                                    for (int i = 0; i < noOfExplosions; i++) {
                                        double angle = (Math.PI * 2 / 8) * i;
                                        double x = this.getX() + Math.cos(angle) * radius;
                                        double z = this.getZ() + Math.sin(angle) * radius;
                                        double y = this.getY();

                                        world.createExplosion(
                                                null,
                                                x, y, z,
                                                12.0f,
                                                World.ExplosionSourceType.BLOCK
                                        );
                                    }
                                }
                            }

                            this.discard();

                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        return super.interactMob(player, hand);
    }

    public static void banOrSpectator(ServerPlayerEntity player) {
        if (!ModConfig.doDominicItemsBanPlayers) {
            player.changeGameMode(GameMode.SPECTATOR);
            return;
        }

        MinecraftServer server = player.getServer();
        if (server == null) return;

        String name = player.getGameProfile().getName();
        // Optional reason text
        String reason = "Your existence was forfeited";

        // Execute vanilla ban command
        ServerCommandSource source = server.getCommandSource();
        server.getCommandManager().executeWithPrefix(source, "ban " + name + " " + reason);

        // Kick immediately (the ban command may also kick, but this ensures it)
        player.networkHandler.disconnect(Text.literal("You are banned!"));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.isOf(DamageTypes.GENERIC_KILL);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            var stack = player.getMainHandStack();
            if (stack.getItem() instanceof CanBanPeopleItem && boundPlayer != null) {
                PlayerEntity bound = getWorld().getPlayerByUuid(boundPlayer);
                if (bound instanceof ServerPlayerEntity serverBound) {
                    serverBound.removeStatusEffect(ModStatusEffects.SOUL_STRAIN);
                }
                this.discard();
                return true;
            }
        }

        if (source.isOf(DamageTypes.GENERIC_KILL)) return super.damage(source, amount);
        return false;
    }


    public static DefaultAttributeContainer.Builder createAttributes() {
        return DefaultAttributeContainer.builder()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 777.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2000.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 0.0);
    }

}