package dev.cxd.dominium.config;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.util.Identifier;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

public class ModConfig extends MidnightConfig {
    public static final String GENERAL = "general";

    @Comment(category = GENERAL, centered = true) public static Comment general;      // Centered comments are the same as normal ones - just centered!

    @Entry(category = GENERAL, name = "Show Dominic Items in The Item Tab?")
    public static boolean showDominicItemsInTab = true;

    @Entry(category = GENERAL, name = "Should Dominic Energy Be Obtained in Survival?")
    public static boolean dominicEnergyInSurvival = false;

    @Comment(category = GENERAL, centered = true) public static Comment soul_candle;      // Centered comments are the same as normal ones - just centered!

    @Entry(category = GENERAL, name = "Soul Candle Radius", min = 1, max = 256)
    public static int SOUL_CANDLE_RADIUS = 64;

    @Comment(category = GENERAL, centered = true) public static Comment experimental;      // Centered comments are the same as normal ones - just centered!
    @Entry(category = GENERAL, name = "Show Experimental Items in The Item Tab? (Not Recommended)")
    public static boolean showExperimentalItemsInTab = false;
}

