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

    @Comment(category = GENERAL, centered = true) public static Comment general;

    @Entry(category = GENERAL, name = "Should Dominic Energy Be Obtained in Survival?")
    public static boolean dominicEnergyInSurvival = false;
    @Entry(category = GENERAL, name = "Should Dominic Items actually ban players instead of putting them in spectator?")
    public static boolean doDominicItemsBanPlayers = false;
    @Entry(category = GENERAL, name = "Should Spectators be able to interact with Doors, Trapdoors, Fence Gates, Levers, Buttons and Note Blocks?")
    public static boolean shouldSpectatorsInteractWithBlocks = true;
    @Entry(category = GENERAL, name = "Show Dominic Items in The Item Tab?")
    public static boolean showDominicItemsInTab = true;

    @Comment(category = GENERAL, centered = true) public static Comment soul_candle;

    @Entry(category = GENERAL, name = "Soul Candle Radius", min = 1, max = 256)
    public static int SOUL_CANDLE_RADIUS = 64;

    @Comment(category = GENERAL, centered = true) public static Comment experimental;
    @Entry(category = GENERAL, name = "Show Experimental Items in The Item Tab? (Not Recommended)")
    public static boolean showExperimentalItemsInTab = false;
}

