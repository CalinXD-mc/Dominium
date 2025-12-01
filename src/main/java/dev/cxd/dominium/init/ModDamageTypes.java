package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {

    public static final RegistryKey<DamageType> DISOBEDIENCE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Dominium.MOD_ID, "disobedience"));
    public static final RegistryKey<DamageType> DOMINIC_BLAST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Dominium.MOD_ID, "dominic_blast"));

    public static void initialize() {

        Dominium.LOGGER.info("Registered Disobedience DamageType");
    }
}
