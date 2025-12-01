package dev.cxd.dominium.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.UUID;

@Config(name = "dominium")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public SomethingUUIDRelated SomeUUIDStuff = new SomethingUUIDRelated();

    @ConfigEntry.Gui.CollapsibleObject
    public SoulCandleRange SoulCandleRange = new SoulCandleRange();

    public static class SomethingUUIDRelated {
        public UUID aRegularUUID = UUID.fromString("1b44461a-f605-4b29-a7a9-04e649d1981c");

    }


    public static class SoulCandleRange {
        public int SOUL_CANDLE_RADIUS = 64;
    }

}
