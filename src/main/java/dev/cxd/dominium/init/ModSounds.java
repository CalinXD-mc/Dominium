package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent WAYSTONE_ACTIVATE = registerSoundEvent("block.waystone.totem_activate");
    public static final SoundEvent WAYSTONE_REPAIR = registerSoundEvent("block.waystone.activate");

    public static final SoundEvent DICE_ROLL = registerSoundEvent("item.dice.roll");
    public static final SoundEvent SOUL_GLASS_PLING = registerSoundEvent("subtitles.soul_glass.soul_glass_pling");

    public static final SoundEvent DOMINIC_BOOM = registerSoundEvent("event.dominium.dominium_boom");

    public static final SoundEvent ROOFLING_AMBIENBT = registerSoundEvent("entity.dominium.roofling_ambient");
    public static final SoundEvent NETHER_GLITCH = registerSoundEvent("nether.dominium.glitch");

    public static final SoundEvent BONE = registerSoundEvent("misc.funny.bone");

    public static final SoundEvent VASSAL_HURT = registerSoundEvent("entity.dominium.vassal_damage");
    public static final SoundEvent VASSAL_DEATH = registerSoundEvent("entity.dominium.vassal_death");
    public static final SoundEvent VASSAL_ATTACK = registerSoundEvent("entity.dominium.vassal_attack");
    public static final SoundEvent VASSAL_AMBIENT = registerSoundEvent("entity.dominium.vassal_ambient");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Dominium.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
        Dominium.LOGGER.info("Initializing Sounds for " + Dominium.MOD_ID);
    }

}
