package dev.cxd.dominium;

import dev.cxd.dominium.command.GhostCommand;
import dev.cxd.dominium.command.MarkerCommand;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.utils.DelayedTaskScheduler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.server.MinecraftServer;
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

		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		SOUL_CANDLE_RANGE = config.SoulCandleRange.SOUL_CANDLE_RADIUS;

		FabricDefaultAttributeRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsEntity.createAttributes());

		ServerTickEvents.END_SERVER_TICK.register(DelayedTaskScheduler::tick);

        CommandRegistrationCallback.EVENT.register(GhostCommand::register);
        CommandRegistrationCallback.EVENT.register(MarkerCommand::register);

        LOGGER.info("Initializing Dominium");
	}
}