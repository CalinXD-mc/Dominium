package dev.cxd.dominium.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ModConfig extends MidnightConfig {
    public static final String GENERAL = "general";

    @Comment(category = GENERAL, centered = true) public static Comment general;

    @Entry(category = GENERAL, name = "Should Dominic Orbs Be Obtainable in Survival by Killing Withers?")
    public static boolean dominicOrbsInSurvival = false;
    @Entry(category = GENERAL, name = "Should Spectators be able to interact with Doors, Trapdoors, Fence Gates, Levers, Buttons and Note Blocks?")
    public static boolean shouldSpectatorsInteractWithBlocks = true;
    @Entry(category = GENERAL, name = "Show Dominic Items in The Item Tab?")
    public static boolean showDominicItemsInTab = true;

    @Comment(category = GENERAL, centered = true) public static Comment soul_candle;

    @Entry(category = GENERAL, name = "Soul Candle Radius", min = 1, max = 256)
    public static int SOUL_CANDLE_RADIUS = 64;

    @Comment(category = GENERAL, centered = true) public static Comment eternal_divinity;

    @Entry(category = GENERAL, name = "Eternal Divinity 1st & 2nd Kills Spectator Time. (in Minutes)", min = 1, max = 2880)
    public static int eternalDivinityBadKillsSpectatorTime = 300;

    @Comment(category = GENERAL, centered = true) public static Comment broken_eternal_divinity;

    @Entry(category = GENERAL, name = "Broken Eternal Divinity Launch Coordonates", min = 77777, max = 7777777)
    public static int brokenEternalDivinityCoordonates = 777777;
    @Entry(category = GENERAL, name = "Should the Broken Eternal Divinity Destroy Itself?")
    public static boolean shouldBrokenEternalDivinityDestroyItself = false;


    @Comment(category = GENERAL, centered = true) public static Comment experimental;
    @Entry(category = GENERAL, name = "Show Experimental Items in The Item Tab? (Not Recommended)")
    public static boolean showExperimentalItemsInTab = false;
}

