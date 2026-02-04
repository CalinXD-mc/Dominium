package dev.cxd.dominium;

import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.entity.RooflingEntity;
import dev.cxd.dominium.init.*;
import dev.cxd.dominium.init.mass_init.DominiumInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Dominium implements ModInitializer {

	public static final String MOD_ID = "dominium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MinecraftServer server;
	public static double BORDER = 0.14;

	public static Set<UUID> GHOST_UUIDS = new HashSet<>();
	public static int SOUL_CANDLE_RANGE;

	public static final Identifier GHOST_SYNC_PACKET_ID = new Identifier(MOD_ID, "ghost_sync");

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		DominiumInitializer.init();

		SOUL_CANDLE_RANGE = ModConfig.SOUL_CANDLE_RADIUS;

		FabricDefaultAttributeRegistry.register(ModEntities.ETERNAL_DIVINITY_CHAINS, EternalDivinityChainsEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ROOFLING, RooflingEntity.createAttributes());

		ResourceManagerHelper.registerBuiltinResourcePack(
                Objects.requireNonNull(Identifier.of(MOD_ID, "glow_in_the_dark")),
				FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
				ResourcePackActivationType.NORMAL
		);

		LOGGER.info("Initializing Dominium");
	}
}