package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.effect.DamnedEffect;
import dev.cxd.dominium.effect.HopelessEffect;
import dev.cxd.dominium.effect.SoulStrainEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {

    public static final StatusEffect SOUL_STRAIN = new SoulStrainEffect();
    public static final StatusEffect DAMNED = new DamnedEffect();
    public static final StatusEffect REGRET = new HopelessEffect();

    public static void initialize() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Dominium.MOD_ID, "soul_strain"), SOUL_STRAIN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Dominium.MOD_ID, "damned"), DAMNED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Dominium.MOD_ID, "regret"), REGRET);

        Dominium.LOGGER.info("Registering Effects For " + Dominium.MOD_ID);
    }

}
