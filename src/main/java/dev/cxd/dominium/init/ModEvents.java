package dev.cxd.dominium.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.block.entity.IdolBlockEntity;
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
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModEvents {
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
            if (world.getRegistryKey() != World.NETHER) return;

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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                GhostSyncPacket.send(handler.player));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            GhostCommand.register(dispatcher, registryAccess, environment);
            MarkerCommand.register(dispatcher, registryAccess, environment);
            NoEnchantZoneCommand.register(dispatcher, registryAccess, environment);
            FactionCommand.register(dispatcher, registryAccess, environment);
        });
    }
}