package dev.cxd.dominium;

import dev.cxd.dominium.command.GhostCommand;
import dev.cxd.dominium.command.MarkerCommand;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.packet.GhostSyncPacket;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import dev.cxd.dominium.utils.GhostManager;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Dominium implements ModInitializer {

	public static final String MOD_ID = "dominium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MinecraftServer server;
	public static double BORDER = 0.14;

	public static Set<UUID> GHOST_UUIDS = new HashSet<>();
	public static int SOUL_CANDLE_RANGE;

	public static final Identifier GHOST_SYNC_PACKET_ID = new Identifier(MOD_ID, "ghost_sync");
	public static final Identifier SOUL_CHAIN_SYNC_PACKET_ID = new Identifier(MOD_ID, "soul_chain_sync");

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModBlockEntities.initialize();
		ModBlocks.initialize();
		ModComponents.initialize();
		ModDamageTypes.initialize();
		ModEntities.initialize();
		ModEvents.initialize();
		ModItems.initialize();
		ModParticles.initialize();
		ModSounds.initialize();
		ModPackets.initializePackets();
		ModItemGroups.initialize();
		ModStatusEffects.initialize();

		MidnightConfig.init(MOD_ID, ModConfig.class);

		SOUL_CANDLE_RANGE = ModConfig.SOUL_CANDLE_RADIUS;

		FabricDefaultAttributeRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsEntity.createAttributes());

		ServerTickEvents.END_SERVER_TICK.register(DelayedTaskScheduler::tick);

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			GHOST_UUIDS = GhostManager.loadGhosts();
			LOGGER.info("Loaded {} ghost player(s)", GHOST_UUIDS.size());
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			GhostManager.saveGhosts(GHOST_UUIDS);
			LOGGER.info("Saved {} ghost player(s)", GHOST_UUIDS.size());
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			GhostSyncPacket.send(handler.player);
		});

		CommandRegistrationCallback.EVENT.register(GhostCommand::register);
		CommandRegistrationCallback.EVENT.register(MarkerCommand::register);

		LOGGER.info("Initializing Dominium");
	}
}