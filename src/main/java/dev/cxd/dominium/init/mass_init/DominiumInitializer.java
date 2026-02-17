package dev.cxd.dominium.init.mass_init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.config.ModConfig;
import dev.cxd.dominium.init.*;
import eu.midnightdust.lib.config.MidnightConfig;

public class DominiumInitializer {
    public static void init() {
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
        MidnightConfig.init(Dominium.MOD_ID, ModConfig.class);
    }
}
