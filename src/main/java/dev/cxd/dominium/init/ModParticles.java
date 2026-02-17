package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {

    public static final DefaultParticleType CHANT = FabricParticleTypes.simple();
    public static final DefaultParticleType DOMINIC_SYMBOL = FabricParticleTypes.simple();
    public static final DefaultParticleType DOMINIC_BALL = FabricParticleTypes.simple();
    public static final DefaultParticleType X_GILDED_ONYX_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType Y_GILDED_ONYX_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType Z_GILDED_ONYX_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType GEAR_GILDED_ONYX_PARTICLE = FabricParticleTypes.simple();

    public static void initialize() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "chant"), CHANT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "dominic_symbol"), DOMINIC_SYMBOL);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "dominic_ball"), DOMINIC_BALL);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "x_gilded_onyx_particle"), X_GILDED_ONYX_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "y_gilded_onyx_particle"), Y_GILDED_ONYX_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "z_gilded_onyx_particle"), Z_GILDED_ONYX_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Dominium.MOD_ID, "gear_gilded_onyx_particle"), GEAR_GILDED_ONYX_PARTICLE);
    }
}