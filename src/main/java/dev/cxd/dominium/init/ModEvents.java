package dev.cxd.dominium.init;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.block.ObeliskBlock;
import dev.cxd.dominium.block.entity.IdolBlockEntity;
import dev.cxd.dominium.block.entity.ObeliskBlockEntity;
import dev.cxd.dominium.block.entity.VesselBlockEntity;
import dev.cxd.dominium.command.FactionCommand;
import dev.cxd.dominium.command.GhostCommand;
import dev.cxd.dominium.command.MarkerCommand;
import dev.cxd.dominium.command.NoEnchantZoneCommand;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.entity.RooflingEntity;
import dev.cxd.dominium.item.necklaces.EtherealNecklaceItem;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.utils.*;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ModEvents {

    @Nullable
    public static ObeliskBlockEntity findClaimAt(World world, BlockPos target) {
        int searchRadius = ObeliskBlockEntity.OBELISK_PROTECTION_RADIUS + 1;
        int searchHeight = ObeliskBlockEntity.OBELISK_PROTECTION_HEIGHT + 1;

        for (BlockPos p : BlockPos.iterateOutwards(target, searchRadius, searchHeight, searchRadius)) {
            BlockState bs = world.getBlockState(p);
            if (bs.getBlock() instanceof ObeliskBlock &&
                    bs.get(ObeliskBlock.PART) == ObeliskBlock.ObeliskPart.LOWER) {
                if (world.getBlockEntity(p) instanceof ObeliskBlockEntity be) {
                    if (be.isInClaimRange(target)) {
                        return be;
                    }
                }
            }
        }
        return null;
    }

    public static void clearStaleMining(World world, ServerPlayerEntity player) {
        var raycast = player.raycast(5.0, 0, false);
        BlockPos currentTarget = null;
        if (raycast instanceof BlockHitResult bhr) {
            currentTarget = bhr.getBlockPos();
        }

        int searchRadius = ObeliskBlockEntity.OBELISK_PROTECTION_RADIUS + 1;
        int searchHeight = ObeliskBlockEntity.OBELISK_PROTECTION_HEIGHT + 1;

        for (BlockPos p : BlockPos.iterateOutwards(player.getBlockPos(), searchRadius, searchHeight, searchRadius)) {
            BlockState bs = world.getBlockState(p);
            if (bs.getBlock() instanceof ObeliskBlock &&
                    bs.get(ObeliskBlock.PART) == ObeliskBlock.ObeliskPart.LOWER) {
                if (world.getBlockEntity(p) instanceof ObeliskBlockEntity be) {
                    be.stopMiningIfStale(world, player.getUuid(), currentTarget);
                }
            }
        }
    }

    public static void initialize() {

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof PlayerEntity target) || player.isCreative()) return ActionResult.PASS;

            for (IdolBlockEntity idol : IdolBlockEntity.getActiveIdols()) {
                if (idol.currentForbiddenUuids.contains(player.getUuid()) && idol.getPlacerUuid() != null) {
                    if (target.getUuid().equals(idol.getPlacerUuid())) {
                        player.sendMessage(Text.literal("You are bound by contract and cannot attack this player!")
                                .formatted(Formatting.RED), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity player) {
                World world = player.getWorld();
                BlockPos pos = player.getBlockPos();
                Block block = world.getBlockState(pos.down()).getBlock();

                if (block == Blocks.SOUL_SAND || block == Blocks.SOUL_SOIL) {
                    int count = 2 + world.getRandom().nextInt(4);
                    ItemStack stack = new ItemStack(ModItems.SOUL_ORB, count);
                    world.spawnEntity(new net.minecraft.entity.ItemEntity(
                            world, player.getX(), player.getY(), player.getZ(), stack));
                }
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (ModConfig.shouldSpectatorsInteractWithBlocks && player.isSpectator()) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();

                if (block instanceof ButtonBlock || block instanceof DoorBlock ||
                        block instanceof TrapdoorBlock || block instanceof FenceGateBlock ||
                        block instanceof LeverBlock || block instanceof BellBlock ||
                        block instanceof NoteBlock) {
                    ActionResult result = state.onUse(world, player, hand, hitResult);
                    return result.isAccepted() ? ActionResult.SUCCESS : ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() == World.NETHER) {
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player.getY() >= ModConfig.NETHER_ROOF_HEIGHT && !world.isSkyVisible(player.getBlockPos())) {
                        boolean wearingEthereal = TrinketsApi.getTrinketComponent(player)
                                .map(tc -> tc.isEquipped(stack -> stack.getItem() instanceof EtherealNecklaceItem))
                                .orElse(false);

                        if (!wearingEthereal) {
                            player.addStatusEffect(new StatusEffectInstance(
                                    StatusEffects.DARKNESS, 200, 0, true, false));
                        }

                        if (world.getRandom().nextInt(4000) == 0) {
                            int count = 1 + world.getRandom().nextInt(2);
                            for (int i = 0; i < count; i++) {
                                double angle = world.getRandom().nextDouble() * Math.PI * 2;
                                double distance = 16 + world.getRandom().nextDouble() * 8;
                                double x = player.getX() + Math.cos(angle) * distance;
                                double z = player.getZ() + Math.sin(angle) * distance;

                                RooflingEntity roofling = ModEntities.ROOFLING.create(world);
                                if (roofling != null) {
                                    roofling.refreshPositionAndAngles(x, player.getY(), z, 0, 0);
                                    world.spawnEntity(roofling);
                                }
                            }
                        }

                        AdvancementsUtils.grantAdvancement(player, "nether_roof");
                    }

                    if (ModConfig.isTheNetherVoidSafe && player.getY() <= ModConfig.NETHER_VOID_HEIGHT) {
                        double targetY = ModConfig.NETHER_ROOF_HEIGHT + 512;
                        player.teleport(player.getX(), targetY, player.getZ());
                        player.setVelocity(0, 0, 0);
                        player.velocityModified = true;
                        player.fallDistance = 0;
                        player.setHealth(1F);

                        player.sendMessage(Text.literal("<???> You shouldn't be here...")
                                .formatted(Formatting.WHITE), true);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.NETHER_GLITCH, net.minecraft.sound.SoundCategory.PLAYERS,
                                1.0F, 0.75F);
                    }
                }
            }
            if (world.getTime() % 40 != 0) return;

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (!player.hasStatusEffect(ModStatusEffects.SOUL_DEBT)) continue;

                BlockPos nearest = null;
                double nearestDist = Double.MAX_VALUE;

                for (VesselBlockEntity vessel : VesselBlockEntity.getActiveVessels()) {
                    if (!vessel.hasContract()) continue;
                    UUID signer = vessel.getSignerUUID();
                    if (!player.getUuid().equals(signer)) continue;

                    double dist = player.squaredDistanceTo(
                            vessel.getPos().getX(), vessel.getPos().getY(), vessel.getPos().getZ());
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest = vessel.getPos();
                    }
                }

                if (nearest != null) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(nearest);
                    ServerPlayNetworking.send(player, ModPackets.SOUL_DEBT_HINT_ID, buf);
                }
            }
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (world.getTime() % 40 != 0) break;
                BlockPos playerPos = player.getBlockPos();
                ObeliskBlockEntity claim = ModEvents.findClaimAt(world, playerPos);
                if (claim == null) continue;
                if (!claim.isAllowed(player.getUuid())) continue;

                UUID ownerUuid = claim.getOwner();
                String ownerName = ownerUuid != null
                        ? (world.getServer().getPlayerManager().getPlayer(ownerUuid) != null
                        ? world.getServer().getPlayerManager().getPlayer(ownerUuid).getName().getString()
                        : ownerUuid.toString())
                        : "Unknown";

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(ownerName);
                ServerPlayNetworking.send(player, ModPackets.CLAIM_PRESENCE_ID, buf);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DelayedTaskScheduler.tick(server);
            ZoneManager.tick(server);
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Dominium.GHOST_UUIDS = GhostManager.loadGhosts();
            FactionManager.loadFactions();
        });

        ServerLifecycleEvents.SERVER_STARTED.register(ZoneManager::initialize);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            GhostManager.saveGhosts(Dominium.GHOST_UUIDS);
            ZoneManager.shutdown();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            GhostSyncPacket.send(handler.player);
            AdvancementsUtils.grantAdvancement(handler.player, "root");
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, blockPos, blockState, blockEntity) -> {
            if (world.isClient()) return true;
            if (blockState.getBlock() instanceof ObeliskBlock) return true;

            ObeliskBlockEntity claim = findClaimAt(world, blockPos);
            if (claim != null && !claim.isAllowed(player.getUuid())) {
                claim.onUnauthorizedBreak(world, blockPos, blockState);
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                return false;
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;
            BlockPos placedAt = hitResult.getBlockPos().offset(hitResult.getSide());

            ObeliskBlockEntity claim = findClaimAt(world, placedAt);
            if (claim != null && !claim.isAllowed(player.getUuid())) {
                BlockState previousState = world.getBlockState(placedAt);

                Objects.requireNonNull(world.getServer()).execute(() -> {
                    BlockState placed = world.getBlockState(placedAt);
                    if (!placed.isAir() && !placed.equals(previousState)) {
                        claim.onUnauthorizedPlace(world, placedAt, previousState);
                    }
                });
                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            GhostCommand.register(dispatcher, registryAccess, environment);
            MarkerCommand.register(dispatcher, registryAccess, environment);
            NoEnchantZoneCommand.register(dispatcher, registryAccess, environment);
            FactionCommand.register(dispatcher, registryAccess, environment);
        });
    }
}