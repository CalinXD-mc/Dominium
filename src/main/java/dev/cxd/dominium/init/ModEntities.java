package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.entity.EternalDivinityChainsEntity;
import dev.cxd.dominium.entity.RooflingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    private static final RegistryKey<EntityType<?>> ETERNAL_DIVINITY_CHAINS_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Dominium.MOD_ID, "eternal_divinity_chains"));

    public static final EntityType<EternalDivinityChainsEntity> ETERNAL_DIVINITY_CHAINS = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Dominium.MOD_ID, "eternal_divinity_chains"),
            EntityType.Builder.create(EternalDivinityChainsEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.9f, 1.8f).build(String.valueOf(ETERNAL_DIVINITY_CHAINS_KEY)));

    private static final RegistryKey<EntityType<?>> ROOFLING_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Dominium.MOD_ID, "roofling"));

    public static final EntityType<RooflingEntity> ROOFLING = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(Dominium.MOD_ID, "roofling"),
            EntityType.Builder.create(RooflingEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.9f, 1.8f).build(String.valueOf(ROOFLING_KEY)));

    public static void initialize() {
        Dominium.LOGGER.info("Registering Mobs for " + Dominium.MOD_ID);
    }

}
